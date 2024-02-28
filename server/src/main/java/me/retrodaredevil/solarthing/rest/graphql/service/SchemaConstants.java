package me.retrodaredevil.solarthing.rest.graphql.service;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class SchemaConstants {
	private SchemaConstants() { throw new UnsupportedOperationException(); }

	public static final String DESCRIPTION_INCLUDE_UNKNOWN_CHANGE = "Set to true to include ChangePackets with unknown values. Default false.";
	public static final String DESCRIPTION_FROM = "The minimum time in milliseconds since the epoch to get data from.";
	public static final String DESCRIPTION_TO = "The maximum time in milliseconds since the epoch to get data from.";
	public static final String DESCRIPTION_OPTIONAL_SOURCE = "The Source ID to include packets from, or null to include packets from multiple sources.";
	public static final String DESCRIPTION_REQUIRED_SOURCE = "The Source ID to include packets from.";
	public static final String DESCRIPTION_FRAGMENT_ID = "The fragment ID to include data from.";

}
