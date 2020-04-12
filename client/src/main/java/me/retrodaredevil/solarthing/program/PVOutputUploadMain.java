package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.PVOutputUploadProgramOptions;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetriever;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.collection.parsing.*;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParametersBuilder;
import me.retrodaredevil.solarthing.pvoutput.service.OkHttpUtil;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputService;
import me.retrodaredevil.solarthing.pvoutput.service.RetrofitUtil;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.extra.DailyMXPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.ektorp.ViewQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.*;

public class PVOutputUploadMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(PVOutputUploadMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.lenientMapper(JacksonUtil.defaultMapper());

	private static PacketGroup getLatestPacket(String sourceId, List<ObjectNode> packetNodes, PacketGroupParser parser){
		List<PacketGroup> rawPacketGroups = new ArrayList<>(packetNodes.size());
		for(ObjectNode object : packetNodes){
			try {
				PacketGroup packetGroup = parser.parse(object);
				rawPacketGroups.add(packetGroup);
			} catch (PacketParseException e) {
				LOGGER.warn("This object must contain all unknown packets...", e);
			}
		}
		Map<String, ? extends List<? extends PacketGroup>> packetGroupsMap = PacketGroups.sortPackets(rawPacketGroups, 2 * 60 * 1000);
		final List<? extends PacketGroup> packetGroups;
		if(sourceId == null){
			if(packetGroupsMap.containsKey("default")){
				packetGroups = packetGroupsMap.get("default");
			} else {
				Iterator<? extends List<? extends PacketGroup>> iterator = packetGroupsMap.values().iterator();
				if(iterator.hasNext()){
					packetGroups = iterator.next();
				} else {
					packetGroups = null;
				}
			}
		} else {
			packetGroups = packetGroupsMap.get(sourceId);
		}
		if(packetGroups == null){
			return null;
		}
		if(packetGroups.isEmpty()){
			return null;
		}
		return packetGroups.get(packetGroups.size() - 1);
	}
	private static AddStatusParameters getStatusParameters(TimeZone timeZone, PacketGroup packetGroup){
		int generatedWH = 0;
		int generatingW = 0;
		int usedWH = 0;
		int usingW = 0;
		for(Packet packet : packetGroup.getPackets()){
			if(packet instanceof DailyMXPacket){
				generatedWH += ((DailyMXPacket) packet).getDailyKWH() * 1000;
			} else if(packet instanceof MXStatusPacket){
				generatingW += ((MXStatusPacket) packet).getPVWattage();
			} else if(packet instanceof FXStatusPacket){
				usingW += ((FXStatusPacket) packet).getInverterWattage();
			} else if(packet instanceof DailyFXPacket){
				usedWH += ((DailyFXPacket) packet).getInverterKWH() * 1000;
			} else if(packet instanceof RoverStatusPacket){
				generatedWH += ((RoverStatusPacket) packet).getDailyKWH() * 1000;
				generatingW += ((RoverStatusPacket) packet).getPVWattage().intValue();
				usedWH += ((RoverStatusPacket) packet).getDailyKWHConsumption() * 1000;
				usingW += ((RoverStatusPacket) packet).getLoadPower();
			}
		}
		LOGGER.debug("generatedWH={} generatingW={} usedWH={} usingW={}", generatedWH, generatingW, usedWH, usingW);

		long timeMillis = packetGroup.getDateMillis();
		Calendar calendar = new GregorianCalendar(timeZone);
		calendar.setTimeInMillis(timeMillis);
		SimpleDate date = SimpleDate.fromCalendar(calendar);
		SimpleTime time = SimpleTime.fromCalendar(calendar);
		return new AddStatusParametersBuilder(date, time)
				.setEnergyGeneration(generatedWH)
				.setPowerGeneration(generatingW)
				.setEnergyConsumption(usedWH)
				.setPowerConsumption(usingW)
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	public static int startPVOutputUpload(PVOutputUploadProgramOptions options){
		LOGGER.info("Starting PV Output upload program");
		TimeZone timeZone = options.getTimeZone();
		LOGGER.debug("Using time zone: {}", timeZone.getDisplayName());
		DatabaseConfig databaseConfig = SolarMain.getDatabaseConfig(options.getDatabase());
		DatabaseType databaseType = databaseConfig.getType();
		if(databaseType != CouchDbDatabaseSettings.TYPE){
			LOGGER.error("(Fatal)Only CouchDb can be used for this program type right now!");
			return 1;
		}
		CouchDbDatabaseSettings couchDbDatabaseSettings = (CouchDbDatabaseSettings) databaseConfig.getSettings();
		CouchProperties couchProperties = couchDbDatabaseSettings.getCouchProperties();
		CouchDbPacketRetriever statusRetriever = new CouchDbPacketRetriever(couchProperties, SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME){
			@Override
			protected ViewQuery alterView(ViewQuery view) {
				return view.startKey(System.currentTimeMillis() - 5 * 60 * 1000); // last 5 minutes
			}
		};
//		CouchDbPacketRetriever eventRetriever = new CouchDbPacketRetriever(couchProperties, SolarThingConstants.SOLAR_EVENT_UNIQUE_NAME){
//			@Override
//			protected ViewQuery alterView(ViewQuery view) {
//				return view.startKey(System.currentTimeMillis() - 15 * 60 * 1000); // last 15 minutes
//			}
//		};
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

		while(!Thread.currentThread().isInterrupted()){
			LOGGER.debug("Going to do stuff now.");
			List<ObjectNode> statusPacketNodes = null;
			try {
				statusPacketNodes = statusRetriever.query();
				LOGGER.debug("Got packets");
			} catch (PacketHandleException e) {
				LOGGER.error("Couldn't get status packets", e);
			}
			if(statusPacketNodes != null){
				PacketGroup packetGroup = getLatestPacket(options.getSourceId(), statusPacketNodes, statusParser);
				if(packetGroup == null){
					LOGGER.warn("latestPacketGroup == null! We didn't get any status packets...");
				} else {
					AddStatusParameters parameters = getStatusParameters(timeZone, packetGroup);
					Call<String> call = service.addStatus(parameters);
					LOGGER.debug("Executing call");
					Response<String> response = null;
					try {
						response = call.execute();
					} catch (IOException e) {
						LOGGER.debug("Exception while executing", e);
					}
					if(response != null) {
						if (response.isSuccessful()) {
							LOGGER.debug("Executed successfully. Result: " + response.body());
						} else {
							LOGGER.debug("Unsuccessful. Message: " + response.message() + " code: " + response.code());
						}
					}
				}
			}
			LOGGER.debug("Going to sleep now");
			try {
				Thread.sleep(5 * 60 * 1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return 1;
	}
}
