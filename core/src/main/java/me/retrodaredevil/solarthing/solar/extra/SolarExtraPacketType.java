package me.retrodaredevil.solarthing.solar.extra;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum SolarExtraPacketType implements DocumentedPacketType {
	FX_DAILY,
	@Deprecated
	FX_CHARGING,

	@Deprecated
	MXFM_DAILY,
}
