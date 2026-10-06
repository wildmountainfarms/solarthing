package me.retrodaredevil.solarthing.solar.outback.fx.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.solar.outback.fx.WarningMode;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public interface FXWarningReporter extends Identifiable {
	int getWarningModeValue();
	@GraphQLInclude("warningModes")
	default Set<WarningMode> getWarningModes(){ return Modes.getActiveModes(WarningMode.class, getWarningModeValue()); }
}
