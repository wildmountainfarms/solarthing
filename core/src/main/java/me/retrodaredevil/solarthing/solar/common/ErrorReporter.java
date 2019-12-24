package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.BitmaskMode;

import java.util.Collection;

public interface ErrorReporter {
	int getErrorMode();
	Collection<? extends BitmaskMode> getErrorModes();
	@Deprecated
	default Collection<? extends BitmaskMode> getActiveErrors(){ return getErrorModes(); }
}
