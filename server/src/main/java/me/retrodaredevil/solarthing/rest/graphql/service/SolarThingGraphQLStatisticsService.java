package me.retrodaredevil.solarthing.rest.graphql.service;

import org.jspecify.annotations.NullMarked;

/**
 * This class is designed to contain queries relating to statistics over a particular time range.
 * <p>
 * Example queries that this should support:
 * <ul>
 *     <li>energy used between 12am and 3am (kWh)</li>
 *     <li>average battery voltage</li>
 * </ul>
 * <p>
 * Hopefully this can one day encompass the logic that {@link SolarThingGraphQLBatteryRecordService} has so we can generalize it better
 *
 * @deprecated Stubbed out for later implementation
 */
@Deprecated
@NullMarked
public class SolarThingGraphQLStatisticsService {


	public class SolarThingStatisticsQuery {

	}
}
