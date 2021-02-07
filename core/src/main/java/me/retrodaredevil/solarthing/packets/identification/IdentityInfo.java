package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
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
	String getDisplayName(); // FX 1, MX 2, Rover 100

	/*
	String get2CharTypeName(); // FX, MX, RV, TR
	String get3CharTypeName(); // FX, MX, RVR, TCR
	String get4CharTypeName(); // FX, MX, ROVR, TRCR
	String getPreferredShortTypeName();
	String getShortTypeName();
	String getTypeName();


	String getLongPrefix();
	String getLongSuffix();

	String getDisplayName(); // Rover // FX 1
	String getShortDisplayName(); // RV 100 // FX 1
	 */
}
