package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

/**
 * The point of this class is to be a very volatile way to represent the name of a device.
 * <p>
 * You probably shouldn't rely on this class to be stable (method names may change)
 * <p>
 * This isn't directly part of {@link Identifier} because {@link Identifier}s can be hashed and compared while this class is just used for display purposes.
 */
@JsonExplicit
@JsonClassDescription("Contains info used to show human readable identifiers")
public interface IdentityInfo {

	@JsonProperty("displayName")
	default String getDisplayName() { // FX 1, MX 2, Rover 40A
		return getName() + " " + getSuffix();
	}
	default boolean isSuffixMeaningful() {
		return true;
	}

	@JsonProperty("name")
	String getName(); // FX
	@JsonProperty("suffix")
	String getSuffix(); // 1

	@JsonProperty("shortName")
	String getShortName(); // FX, RV, WND, DCC

	@GraphQLInclude("stripExtra")
	default IdentityInfo stripExtra() {
		return this;
	}
}
