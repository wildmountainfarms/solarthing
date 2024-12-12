package me.retrodaredevil.solarthing.program.subprogram.analyze.analyzers.generator.entry;

import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

/**
 * Statistics over some period of time for a single FX.
 * {@link me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket}
 *
 * Depending on what was happening during the time period this represents (AC Use, AC Drop, Silent, Bulk, the entire generator run time),
 * some properties may not be useful.
 * Usually many of these properties become more useful for the shorter periods of time that are focused on.
 *
 * @param identifier
 * @param averageChargerWattage
 * @param averageBuyWattage
 * @deprecated Not yet implemented
 */
@Deprecated
record GeneratorFXStatistics(
		OutbackIdentifier identifier,
		float averageInverterCurrent,
		float averageInverterWattage,

		float averageChargerCurrent,
		float averageChargerWattage,

		float averageBuyCurrent,
		float averageBuyWattage
) {}
