package me.retrodaredevil.solarthing.program.subprogram.pvoutput;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.analytics.AnalyticsManager;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.PVOutputUploadProgramOptions;
import me.retrodaredevil.solarthing.config.options.ProgramType;
import me.retrodaredevil.solarthing.database.MillisQueryBuilder;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.program.subprogram.run.CommandOptions;
import me.retrodaredevil.solarthing.config.ConfigUtil;
import me.retrodaredevil.solarthing.config.databases.DatabaseConfig;
import me.retrodaredevil.solarthing.program.PacketUtil;
import me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider.TemperatureCelsiusProvider;
import me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider.VoltageProvider;
import me.retrodaredevil.solarthing.pvoutput.CsvUtil;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.data.AddBatchOutputParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParametersBuilder;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;
import me.retrodaredevil.solarthing.pvoutput.data.ImmutableAddBatchOutputParameters;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputOkHttpUtil;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputRetrofitUtil;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputService;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationConfig;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PVOutputUploadMain {
	private PVOutputUploadMain(){ throw new UnsupportedOperationException(); }
	private static final Logger LOGGER = LoggerFactory.getLogger(PVOutputUploadMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.lenientMapper(JacksonUtil.defaultMapper());

	// TODO Make this an action for the automation program


	@SuppressWarnings({"SameReturnValue"})
	public static int startPVOutputUpload(PVOutputUploadProgramOptions options, CommandOptions commandOptions, boolean isValidate){

		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Starting PV Output upload program");
		ZoneId zoneId = options.getZoneId();
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Using time zone: {}", zoneId.getDisplayName(TextStyle.FULL, Locale.US)); // Use US local since I (retrodaredevil) am the one debugging
		LOGGER.info("Using default instance options: " + options.getDefaultInstanceOptions());
		DatabaseConfig databaseConfig = ConfigUtil.readDatabaseConfig(options.getDatabaseFilePath());
		DatabaseType databaseType = databaseConfig.getType();
		if(databaseType != CouchDbDatabaseSettings.TYPE){
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)Only CouchDb can be used for this program type right now!");
			return SolarThingConstants.EXIT_CODE_INVALID_CONFIG;
		}
		CouchDbDatabaseSettings couchDbDatabaseSettings = (CouchDbDatabaseSettings) databaseConfig.requireDatabaseSettings();
		SolarThingDatabase database = CouchDbSolarThingDatabase.create(CouchDbUtil.createInstance(couchDbDatabaseSettings.getCouchProperties(), couchDbDatabaseSettings.getOkHttpProperties()));

		OkHttpClient client = PVOutputOkHttpUtil.configure(new OkHttpClient.Builder(), options.getApiKey(), options.getSystemId())
				.addInterceptor(new HttpLoggingInterceptor(LOGGER::debug).setLevel(HttpLoggingInterceptor.Level.BASIC))
				.build();
		Retrofit retrofit = PVOutputRetrofitUtil.defaultBuilder().client(client).build();
		PVOutputService service = retrofit.create(PVOutputService.class);
		VoltageProvider voltageProvider = options.getVoltageProvider();
		TemperatureCelsiusProvider temperatureCelsiusProvider = options.getTemperatureCelsiusProvider();
		PVOutputHandler handler = new PVOutputHandler(zoneId, options.getRequiredIdentifierMap(), voltageProvider, temperatureCelsiusProvider);

		String fromDateString = commandOptions.getPVOutputFromDate();
		String toDateString = commandOptions.getPVOutputToDate();
		if(fromDateString != null && toDateString != null) {
			System.out.println("Starting range upload");
			final LocalDate fromDate;
			final LocalDate toDate;
			try {
				// TODO Don't use SimpleDateFormat anymore and remove supress warnings for deprecation
				fromDate = LocalDate.parse(fromDateString, DateTimeFormatter.ISO_LOCAL_DATE);
				toDate = LocalDate.parse(toDateString, DateTimeFormatter.ISO_LOCAL_DATE);
			} catch (DateTimeParseException e) {
				e.printStackTrace();
				System.err.println("Unable to parser either from date or to date. Use the yyyy-MM-dd format");
				return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
			}
			if (isValidate) {
				return 0;
			}
			return startRangeUpload(
					fromDate, toDate,
					options, database, handler, service, options.getZoneId()
			);
		} else if ((fromDateString == null) != (toDateString == null)) {
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Fatal)You need to define both from and to, or define neither to do the normal PVOutput program!");
			return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
		}
		AnalyticsManager analyticsManager = new AnalyticsManager(options.isAnalyticsEnabled());

		if (isValidate) {
			return 0;
		}
		analyticsManager.sendStartUp(ProgramType.PVOUTPUT_UPLOAD);
		return startRealTimeProgram(options, database, handler, service, options.getZoneId());
	}
	@SuppressWarnings("CatchAndPrintStackTrace")
	private static int startRangeUpload(
			LocalDate fromDate, LocalDate toDate,
			PVOutputUploadProgramOptions options, SolarThingDatabase database,
			PVOutputHandler handler, PVOutputService service, ZoneId zoneId
	) {
		List<AddOutputParameters> addOutputParameters = new ArrayList<>();
		for(LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) { // toDate is inclusive
			System.out.println("Doing " + date);
			Instant dayStart = date.atStartOfDay(zoneId).toInstant();
			Instant dayEnd = date.plusDays(1).atStartOfDay(zoneId).toInstant();

			List<? extends PacketGroup> rawPacketGroups = null;
			try {
				rawPacketGroups = database.getStatusDatabase().query(new MillisQueryBuilder()
						.startKey(dayStart.toEpochMilli())
						.endKey(dayEnd.toEpochMilli())
						.inclusiveEnd(false)
						.build()
				);
				System.out.println("Got " + rawPacketGroups.size() + " packets for date: " + date);
			} catch (SolarThingDatabaseException e) {
				e.printStackTrace();
				System.err.println("Couldn't query packets. Skipping " + date);
			}
			if (rawPacketGroups != null) {
				List<FragmentedPacketGroup> packetGroups = PacketUtil.getPacketGroups(options.getSourceId(), options.getDefaultInstanceOptions(), rawPacketGroups);

				if (packetGroups != null) {
					if (!handler.checkPackets(dayStart.toEpochMilli(), packetGroups)) {
						System.err.println("Unsuccessfully checked packets for " + date);
						try {
							System.out.println(MAPPER.writeValueAsString(packetGroups.get(packetGroups.size() - 1)));
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
					} else {
						AddStatusParameters statusParameters = handler.getStatus(dayStart.toEpochMilli(), packetGroups);
						AddOutputParametersBuilder outputParametersBuilder = new AddOutputParametersBuilder(statusParameters.getDate())
								.setGenerated(statusParameters.getEnergyGeneration())
								.setConsumption(statusParameters.getEnergyConsumption());
						PVOutputHandler.setImportedExported(outputParametersBuilder, packetGroups, AccumulationConfig.createDefault(dayStart.toEpochMilli()), options.isIncludeImport(), options.isIncludeExport());
						AddOutputParameters outputParameters = outputParametersBuilder.build();
						addOutputParameters.add(outputParameters);
						System.out.println("Added parameters for " + date + " to queue.");
						System.out.println("Generated: " + statusParameters.getEnergyGeneration());
						System.out.println(Arrays.toString(outputParameters.toCsvArray()));
						System.out.println(CsvUtil.toCsvString(outputParameters.toCsvArray()));
					}
				} else {
					System.err.println("Didn't find any packets with source: " + options.getSourceId() + " for date: " + date);
				}
			}
		}
		System.out.println("Going to upload in batches of 30...");
		for (int i = 0; i < addOutputParameters.size(); i += 30) {
			if (i != 0) {
				System.out.println("Sleeping...");
				try {
					//noinspection BusyWait
					Thread.sleep(7000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.err.println("Interrupted");
					return SolarThingConstants.EXIT_CODE_INTERRUPTED;
				}
			}
			int endIndex = Math.min(i + 30, addOutputParameters.size());
			List<AddOutputParameters> parameters = addOutputParameters.subList(i, endIndex);
			System.out.println("Going to upload from " + parameters.get(0).getOutputDate().toPVOutputString() + " to " + parameters.get(parameters.size() - 1).getOutputDate().toPVOutputString());
			AddBatchOutputParameters batchOutputParameters = new ImmutableAddBatchOutputParameters(parameters);
			try {
				LOGGER.debug("Batch Output parameters as JSON: " + MAPPER.writeValueAsString(batchOutputParameters));
			} catch (JsonProcessingException e) {
				LOGGER.error("Got error serializing JSON. This should never happen.", e);
			}
			boolean successful = false;
			for (long j = 0; j < 5; j++) {
				if (j != 0) {
					System.out.println("Sleeping before trying again");
					try {
						//noinspection BusyWait
						Thread.sleep(j * 7000L);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						System.err.println("Interrupted");
						return SolarThingConstants.EXIT_CODE_INTERRUPTED;
					}
				}
				Call<String> call = service.addBatchOutput(batchOutputParameters);
				final Response<String> response;
				try {
					response = call.execute();
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Error while executing");
					continue;
				}
				if (response.isSuccessful()) {
					System.out.println("Executed successfully. Result: " + response.body());
					successful = true;
					break;
				} else {
					System.err.println("Unsuccessful. Message: " + response.message() + " code: " + response.code());
				}
			}
			if (!successful) {
				System.err.println("All tries were unsuccessful. Ending");
				return SolarThingConstants.EXIT_CODE_FAIL;
			}
		}
		System.out.println("Done!");
		return 0;
	}

	private static int startRealTimeProgram(
			PVOutputUploadProgramOptions options, SolarThingDatabase database,
			PVOutputHandler handler, PVOutputService service, ZoneId zoneId
	) {
		if (options.isJoinTeams()) {
			LOGGER.info("Going to join SolarThing team...");
			Call<String> call = service.joinTeam(PVOutputConstants.SOLARTHING_TEAM_ID);
			LOGGER.debug("Executing call");
			Response<String> response = null;
			try {
				response = call.execute();
			} catch (IOException e) {
				LOGGER.error("Exception while executing", e);
			}
			if (response != null) {
				int code = response.code();
				String errorBody;
				try {
					ResponseBody responseBody = response.errorBody();
					if (responseBody != null) {
						errorBody = responseBody.string();
					} else {
						errorBody = "null";
					}
				} catch (IOException e) {
					e.printStackTrace();
					errorBody = "exception occurred";
				}
				if (code == 200) {
					LOGGER.info("Joined the SolarThing team! Response: " + response.body());
				} else if (code == 400) {
					if (errorBody.contains("already")) {
						LOGGER.info("Already joined SolarThing team. Response: " + errorBody);
					} else if (errorBody.contains("must have at least")) {
						LOGGER.info("We will try joining SolarThing team later once we have more outputs. Response: " + errorBody);
					} else {
						LOGGER.error("Error joining SolarThing team! Response: " + errorBody);
					}
				} else {
					LOGGER.error("Unknown error joining SolarThing team! Response: " + errorBody);
				}
			}
		}
		while(!Thread.currentThread().isInterrupted()){
			LOGGER.debug("Going to do stuff now.");
			long now = System.currentTimeMillis();
			SimpleDate today = SimpleDate.fromDateMillis(now, zoneId);
			long dayStartTimeMillis = today.getDayStartDateMillis(zoneId);
			List<? extends PacketGroup> rawPacketGroups = null;
			try {
				rawPacketGroups = database.getStatusDatabase().query(new MillisQueryBuilder()
						.startKey(dayStartTimeMillis)
						.endKey(now)
						.build()
				);
				LOGGER.debug("Got packets");
			} catch (SolarThingDatabaseException e) {
				LOGGER.error("Couldn't get status packets", e);
			}
			if(rawPacketGroups != null){
				List<FragmentedPacketGroup> packetGroups = PacketUtil.getPacketGroups(options.getSourceId(), options.getDefaultInstanceOptions(), rawPacketGroups);
				if (packetGroups != null) {
					FragmentedPacketGroup latestPacketGroup = packetGroups.get(packetGroups.size() - 1);
					if (latestPacketGroup.getDateMillis() < now - 5 * 60 * 1000) {
						LOGGER.warn("The last packet is more than 5 minutes in the past! now=" + now + " packet date=" + latestPacketGroup.getDateMillis());
						try {
							LOGGER.debug("Packets: " + MAPPER.writeValueAsString(latestPacketGroup.getPackets()));
						} catch (JsonProcessingException e) {
							LOGGER.warn("Couldn't serialize for some reason", e);
						}
					} else if (!handler.checkPackets(dayStartTimeMillis, packetGroups)){
						LOGGER.warn("Checking packets unsuccessful.");
					} else {
						AddStatusParameters parameters = handler.getStatus(dayStartTimeMillis, packetGroups);
						if (uploadStatus(service, parameters) && (options.isIncludeImport() || options.isIncludeExport())) {
							// only upload output if status is successful
							AddOutputParametersBuilder outputParametersBuilder = new AddOutputParametersBuilder(parameters.getDate());
							PVOutputHandler.setImportedExported(outputParametersBuilder, packetGroups, AccumulationConfig.createDefault(dayStartTimeMillis), options.isIncludeImport(), options.isIncludeExport());
							AddOutputParameters outputParameters = outputParametersBuilder.build();
							uploadOutput(service, outputParameters);
						}
					}
				} else {
					LOGGER.warn("Got " + rawPacketGroups.size() + " packets but, there must not have been any packets with the source: " + options.getSourceId());
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
		return SolarThingConstants.EXIT_CODE_INTERRUPTED;
	}
	private static boolean uploadStatus(PVOutputService service, AddStatusParameters addStatusParameters) {
		try {
			LOGGER.debug("Status parameters as JSON: " + MAPPER.writeValueAsString(addStatusParameters));
		} catch (JsonProcessingException e) {
			LOGGER.error("Got error serializing JSON. This should never happen.", e);
		}
		Call<String> call = service.addStatus(addStatusParameters);
		return executeCall(call);
	}
	private static boolean uploadOutput(PVOutputService service, AddOutputParameters addOutputParameters) {
		try {
			LOGGER.debug("Output parameters as JSON: " + MAPPER.writeValueAsString(addOutputParameters));
		} catch (JsonProcessingException e) {
			LOGGER.error("Got error serializing JSON. This should never happen.", e);
		}
		Call<String> call = service.addOutput(addOutputParameters);
		return executeCall(call);
	}
	private static boolean executeCall(Call<String> call) {
		LOGGER.debug("Executing call");
		final Response<String> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			LOGGER.error("Exception while executing", e);
			return false;
		}
		if (response.isSuccessful()) {
			LOGGER.debug("Executed successfully. Result: " + response.body());
			return true;
		}
		LOGGER.debug("Unsuccessful. Message: " + response.message() + " code: " + response.code());
		return false;
	}
}
