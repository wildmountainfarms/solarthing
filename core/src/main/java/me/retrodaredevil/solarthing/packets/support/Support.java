package me.retrodaredevil.solarthing.packets.support;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

/**
 * Represents whether or not something is fully supported
 * <p>
 * NOTE: Do not change the implementation of {@link #toString()} in the future. That may cause side effects
 */
public enum Support {
	FULLY_SUPPORTED,
	NOT_SUPPORTED,
	@JsonEnumDefaultValue
	UNKNOWN
}
