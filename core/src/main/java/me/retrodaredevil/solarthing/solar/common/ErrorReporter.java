package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.packets.BitmaskMode;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public interface ErrorReporter extends Identifiable {
	/**
	 * Although this is usually serialized, there is no standard key for serialization. "errorMode" and "errorModeValue" are commonly used.
	 * @return The value of the error mode.
	 */
	int getErrorModeValue();
	// TODO remove NonNull
	@GraphQLInclude("errorModes")
	@NonNull Collection<? extends @NonNull BitmaskMode> getErrorModes();
	@GraphQLInclude("hasError")
	default boolean hasError() {
		return getErrorModeValue() != 0;
	}
}
