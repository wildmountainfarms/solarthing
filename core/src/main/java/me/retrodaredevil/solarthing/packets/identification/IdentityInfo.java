package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;

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
	// Note the JsonProperty annotations are for GraphQL. These are not meant to be serialized and saved to a database

	@JsonProperty("displayName")
	default String getDisplayName() { // FX 1, MX 2, Rover 40A
		String suffix = getSuffix();
		if (suffix.isEmpty()) {
			return getName();
		}
		return getName() + " " + suffix;
	}

	// Looking at this on 2021.06.05, I don't even know how to define if a suffix is meaningful or not...
	default boolean isSuffixMeaningful() {
		return true;
	}

	@JsonProperty("name")
	@NotNull String getName(); // FX

	/**
	 * @return The suffix or a blank string
	 */
	@JsonProperty("suffix")
	@NotNull String getSuffix(); // 1

	@JsonProperty("shortName")
	@NotNull String getShortName(); // FX, RV, WND, DCC

	/**
	 * This is useful if you want to display the name of something, but one place has extra information and the other does not. For instance,
	 * rover status packets know the rover is actually a wanderer, but an event packet associated with that rover/wanderer may not know that.
	 * You can use this to get rid of extra information so any identical device's {@link IdentityInfo} is guaranteed to have the same properties.
	 * @return An {@link IdentityInfo} object of the same type without extra information.
	 */
	@GraphQLInclude("stripExtra")
	default IdentityInfo stripExtra() {
		return this;
	}
}
