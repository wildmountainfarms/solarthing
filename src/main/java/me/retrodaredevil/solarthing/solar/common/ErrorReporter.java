package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.BitmaskMode;

import java.util.Collection;

public interface ErrorReporter {
	int getErrorMode();
	Collection<? extends BitmaskMode> getActiveErrors();
}
