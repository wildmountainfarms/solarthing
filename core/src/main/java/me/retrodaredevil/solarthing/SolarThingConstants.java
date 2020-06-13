package me.retrodaredevil.solarthing;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public final class SolarThingConstants {
	private SolarThingConstants(){ throw new UnsupportedOperationException(); }

	public static final String SOLAR_STATUS_UNIQUE_NAME = "solarthing";
	public static final String SOLAR_EVENT_UNIQUE_NAME = "solarthing_events";
	@Deprecated
	public static final String COMMANDS_UNIQUE_NAME = "commands";
	public static final String OPEN_UNIQUE_NAME = "solarthing_open";
	public static final String CLOSED_UNIQUE_NAME = "solarthing_closed";

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
}
