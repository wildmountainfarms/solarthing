package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.time.Duration;

@UtilityClass
public final class SolarThingConstants {
	private SolarThingConstants(){ throw new UnsupportedOperationException(); }

	public static final String STATUS_DATABASE = "solarthing";
	public static final String EVENT_DATABASE = "solarthing_events";
	public static final String OPEN_DATABASE = "solarthing_open";
	public static final String CLOSED_DATABASE = "solarthing_closed";
	public static final String CACHE_DATABASE = "solarthing_cache";
	@Deprecated public static final String SOLAR_STATUS_UNIQUE_NAME = "solarthing";
	@Deprecated public static final String SOLAR_EVENT_UNIQUE_NAME = "solarthing_events";
	@Deprecated public static final String OPEN_UNIQUE_NAME = "solarthing_open";
	@Deprecated public static final String CLOSED_UNIQUE_NAME = "solarthing_closed";
	@Deprecated public static final String CACHE_UNIQUE_NAME = "solarthing_cache";

	// for documentation on markers: https://logging.apache.org/log4j/2.0/manual/filters.html#MarkerFilter
	/**
	 * This is a marker used to put something in the "summary" log file. This log file is used for events that don't happen
	 * frequently. (SolarThing program is starting, a command is requested, etc)
	 */
	public static final Marker SUMMARY_MARKER = MarkerFactory.getMarker("SUMMARY");

	/**
	 * This can be used to make sure something doesn't go to the console. This is useful for debugs that aren't important
	 */
	public static final Marker NO_CONSOLE = MarkerFactory.getMarker("NO_CONSOLE");


	/** Duration for how long to query back in time to get only the latest packets*/
	public static final Duration LATEST_PACKETS_DURATION = Duration.ofMinutes(12);

	public static final Duration STANDARD_MAX_TIME_DISTANCE = Duration.ofMinutes(8);
	public static final Duration STANDARD_MASTER_ID_IGNORE_DISTANCE = Duration.ofMinutes(4);

	public static final Duration SHORT_MAX_TIME_DISTANCE = Duration.ofMinutes(4);
	public static final Duration SHORT_MASTER_ID_IGNORE_DISTANCE = Duration.ofMinutes(2);
}
