package me.retrodaredevil.solarthing.solar.outback.fx.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.outback.fx.WarningMode;

import javax.validation.constraints.NotNull;
import java.util.Set;

public interface FXWarningReporter {
	int getWarningModeValue();
	@GraphQLInclude("warningModes")
	default @NotNull Set<@NotNull WarningMode> getWarningModes(){ return Modes.getActiveModes(WarningMode.class, getWarningModeValue()); }
}
