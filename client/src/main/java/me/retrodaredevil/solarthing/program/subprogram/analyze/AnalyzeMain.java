package me.retrodaredevil.solarthing.program.subprogram.analyze;

import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.config.ConfigException;
import me.retrodaredevil.solarthing.config.ConfigUtil;
import me.retrodaredevil.solarthing.config.databases.DatabaseConfig;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.program.subprogram.analyze.analyzers.generator.GeneratorRunAnalyzer;
import me.retrodaredevil.solarthing.program.subprogram.analyze.analyzers.generator.entry.GeneratorRunEntry;
import me.retrodaredevil.solarthing.program.subprogram.analyze.analyzers.generator.entry.GeneratorStatistics;
import me.retrodaredevil.solarthing.program.subprogram.analyze.statistics.fx.FXStatisticCollection;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public final class AnalyzeMain {
	private AnalyzeMain() { throw new UnsupportedOperationException(); }

	/**
	 * Parses a string to an Instant, trying multiple ways of parsing the string to support multiple format options of the string we are trying to parse.
	 *
	 * @param timeString The string to parse
	 * @param zoneId The zone ID to use if a local date is parsed. This timezone will be used to convert the local date to midnight in this timezone on that given day.
	 * @param daysToAddToLocalDate If a local date is parsed (YYYY-MM-dd), then this number of days will be added to that local date.
	 * @return The instant parsed from the given options
	 */
	public static Instant parseTime(String timeString, ZoneId zoneId, long daysToAddToLocalDate) {
		try {
			long dateMillis = Long.parseLong(timeString);
			return Instant.ofEpochMilli(dateMillis);
		} catch (NumberFormatException ignored) {
		}
		try {
			LocalDate date = LocalDate.parse(timeString, DateTimeFormatter.ISO_LOCAL_DATE);
			return date.plusDays(daysToAddToLocalDate).atStartOfDay(zoneId).toInstant();
		} catch (DateTimeParseException ignored) {
		}
		return Instant.parse(timeString);
	}

	public static int analyze(String[] args) {
		Options options = getOptions();

		CommandLineParser parser = new DefaultParser();
		final CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(new PrintWriter(System.err, false, Charset.defaultCharset()), formatter.getWidth(), "solarthing analyze", null, options, formatter.getLeftPadding(), formatter.getDescPadding(), null);

			return 1;
		}

		String couchDbConfigurationFilePathString = cmd.getOptionValue("couchdb");
		String startString = cmd.getOptionValue("start");
		String endString = cmd.getOptionValue("end");
		String fragmentIdString = cmd.getOptionValue("fragment");

		// TODO don't use system default ZoneId (maybe there's a better way of passing it in?)
		ZoneId zoneId = ZoneId.systemDefault();
		Path couchDbConfig = Path.of(couchDbConfigurationFilePathString);
		Instant startTime = parseTime(startString, zoneId, 0);
		Instant endTime = parseTime(endString, zoneId, 1);
		int fragmentId = Integer.parseInt(fragmentIdString);


		DatabaseConfig databaseConfig;
		try {
			databaseConfig = ConfigUtil.readDatabaseConfig(couchDbConfig);
		} catch (ConfigException e) {
			e.printStackTrace(System.err);
			System.err.println(e.getMessage());
			return SolarThingConstants.EXIT_CODE_INVALID_CONFIG;
		}
		if(databaseConfig.getType() != CouchDbDatabaseSettings.TYPE){
			System.err.println("You must provide CouchDB options!");
			return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
		}
		CouchDbDatabaseSettings couchDbDatabaseSettings = (CouchDbDatabaseSettings) databaseConfig.requireDatabaseSettings();
		SolarThingDatabase database = CouchDbSolarThingDatabase.create(CouchDbUtil.createInstance(couchDbDatabaseSettings.getCouchProperties(), couchDbDatabaseSettings.getOkHttpProperties()));

		AnalysisExecutor<GeneratorRunEntry> executor = new AnalysisExecutor<>(
				database,
				// TODO configure default instance options
				new GeneratorRunAnalyzer(DefaultInstanceOptions.REQUIRE_NO_DEFAULTS, fragmentId),
				Duration.ofHours(24),
				Duration.ofHours(2),
				Duration.ofHours(16)
		);

		System.out.println("Going to start analysis from " + startTime + " to " + endTime + " for fragment ID: " + fragmentId);
		try {
			List<GeneratorRunEntry> rows = executor.analyze(startTime, endTime);
			for (GeneratorRunEntry entry : rows) {
				GeneratorStatistics stats = entry.wholeStatistics();
				String details = stats.getFXAddresses().stream()
						.sorted()
						.map(address -> {
							FXStatisticCollection fxStats = stats.getStatistics(address);

							return "\n\tFX" + address
									+ "  Voltage: mean: " + fxStats.stats().inputVoltageRaw().mean() + " upper 99%: " + fxStats.percentiles().inputVoltageRaw().p99() + " lower 1%: " + fxStats.percentiles().inputVoltageRaw().p1()
									;
						})
						.collect(Collectors.joining(""));
				System.out.println("Entry:  Start: " + entry.startTime().atZone(zoneId) + " End: " + entry.endTime().atZone(zoneId) + " duration: " + Duration.between(entry.startTime(), entry.endTime()) + details);
			}
		} catch (AnalysisException e) {
			e.getCause().printStackTrace(System.err);
			System.err.println();
			System.err.println(e.getMessage());
			return 1;
		}

		return 0;
	}

	private static @NotNull Options getOptions() {
		Options options = new Options();

		Option input = new Option(null, "couchdb", true, "CouchDB database configuration file path");
		input.setRequired(true);
		options.addOption(input);

		Option startTime = new Option(null, "start", true, "The start time formatted as either YYYY-MM-dd or ISO 8601 time.");
		startTime.setRequired(true);
		options.addOption(startTime);

		Option endTime = new Option(null, "end", true, "The end time formatted as either YYYY-MM-dd or ISO 8601 time.");
		endTime.setRequired(true);
		options.addOption(endTime);

		Option fragmentId = new Option(null, "fragment", true, "The fragment ID that the FX(s) are associated with.");
		fragmentId.setRequired(true);
		options.addOption(fragmentId);

		return options;
	}
}
