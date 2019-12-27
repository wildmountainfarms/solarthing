package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.BitmaskMode;

import java.util.Collection;

public interface ErrorReporter {
	/**
	 * Although this is usually serialized, there is no standard key for serialization. "errorMode" and "errorModeValue" are commonly used.
	 * @return The value of the error mode.
	 */
	int getErrorModeValue();
	Collection<? extends BitmaskMode> getErrorModes();
	@Deprecated
	default Collection<? extends BitmaskMode> getActiveErrors(){ return getErrorModes(); }
}
