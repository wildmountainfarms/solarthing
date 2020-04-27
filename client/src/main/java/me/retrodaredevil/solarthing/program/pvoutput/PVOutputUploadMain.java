package me.retrodaredevil.solarthing.program.pvoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.EktorpUtil;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.PVOutputUploadProgramOptions;
import me.retrodaredevil.solarthing.couchdb.CouchDbQueryHandler;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.collection.parsing.*;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.program.DatabaseConfig;
import me.retrodaredevil.solarthing.program.SolarMain;
import me.retrodaredevil.solarthing.pvoutput.service.OkHttpUtil;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputService;
import me.retrodaredevil.solarthing.pvoutput.service.RetrofitUtil;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;

import java.util.*;

public class PVOutputUploadMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(PVOutputUploadMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.lenientMapper(JacksonUtil.defaultMapper());

	private static List<FragmentedPacketGroup> getPacketGroups(String sourceId, List<? extends ObjectNode> packetNodes, PacketGroupParser parser){
		Map<String, List<FragmentedPacketGroup>> packetGroupsMap = PacketGroups.sortPackets(PacketParseUtil.parseRawPackets(packetNodes, parser), 2 * 60 * 1000);
		if(sourceId == null){
			if(packetGroupsMap.containsKey("default")){
				return packetGroupsMap.get("default");
			} else {
				Iterator<List<FragmentedPacketGroup>> iterator = packetGroupsMap.values().iterator();
				if(iterator.hasNext()){
					return iterator.next();
				} else {
					return null;
				}
			}
		}
		return packetGroupsMap.get(sourceId);
	}

	@SuppressWarnings("SameReturnValue")
	public static int startPVOutputUpload(PVOutputUploadProgramOptions options){
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Starting PV Output upload program");
		TimeZone timeZone = options.getTimeZone();
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Using time zone: {}", timeZone.getDisplayName());
		DatabaseConfig databaseConfig = SolarMain.getDatabaseConfig(options.getDatabase());
		DatabaseType databaseType = databaseConfig.getType();
		if(databaseType != CouchDbDatabaseSettings.TYPE){
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Only CouchDb can be used for this program type right now!");
			return 1;
		}
		CouchDbDatabaseSettings couchDbDatabaseSettings = (CouchDbDatabaseSettings) databaseConfig.getSettings();
		CouchProperties couchProperties = couchDbDatabaseSettings.getCouchProperties();
		final CouchDbQueryHandler queryHandler;
		{
			final HttpClient httpClient = EktorpUtil.createHttpClient(couchProperties);
			CouchDbInstance instance = new StdCouchDbInstance(httpClient);
			queryHandler = new CouchDbQueryHandler(new StdCouchDbConnector(SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME, instance), false);
		}
		PacketGroupParser statusParser = new SimplePacketGroupParser(new PacketParserMultiplexer(Arrays.asList(
				new ObjectMapperPacketConverter(MAPPER, SolarStatusPacket.class),
				new ObjectMapperPacketConverter(MAPPER, SolarExtraPacket.class),
				new ObjectMapperPacketConverter(MAPPER, InstancePacket.class)
		), PacketParserMultiplexer.LenientType.FULLY_LENIENT));

		OkHttpClient client = OkHttpUtil.configure(new OkHttpClient.Builder(), options.getApiKey(), options.getSystemId())
				.addInterceptor(new HttpLoggingInterceptor(LOGGER::debug).setLevel(HttpLoggingInterceptor.Level.BASIC))
				.build();
		Retrofit retrofit = RetrofitUtil.defaultBuilder().client(client).build();
		PVOutputService service = retrofit.create(PVOutputService.class);

		PVOutputHandler handler = new PVOutputHandler(timeZone, options.getRequiredFragmentIds(), options.getRequiredIdentifiers(), service);

		while(!Thread.currentThread().isInterrupted()){
			LOGGER.debug("Going to do stuff now.");
			long now = System.currentTimeMillis();
			Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
			calendar.setTimeInMillis(now);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			calendar.setTimeInMillis(calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 1000));
			long dayStartTimeMillis = calendar.getTimeInMillis();
			List<ObjectNode> statusPacketNodes = null;
			try {
				statusPacketNodes = queryHandler.query(new ViewQuery()
						.designDocId("_design/packets")
						.viewName("millis")
						.startKey(dayStartTimeMillis)
						.endKey(now));
				LOGGER.debug("Got packets");
			} catch (PacketHandleException e) {
				LOGGER.error("Couldn't get status packets", e);
			}
			if(statusPacketNodes != null){
				List<FragmentedPacketGroup> packetGroups = getPacketGroups(options.getSourceId(), statusPacketNodes, statusParser);
				if (packetGroups != null) {
					handler.handle(dayStartTimeMillis, packetGroups);
				} else {
					LOGGER.warn("Got " + statusPacketNodes.size() + " packets but, there must not have been any packets with the source: " + options.getSourceId());
				}
			}
			LOGGER.debug("Going to sleep now");
			try {
				//noinspection BusyWait
				Thread.sleep(5 * 60 * 1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return 1;
	}
}
