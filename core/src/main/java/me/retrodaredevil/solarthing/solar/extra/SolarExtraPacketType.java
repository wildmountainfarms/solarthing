package me.retrodaredevil.solarthing.solar.extra;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;

public enum SolarExtraPacketType implements DocumentedPacketType {
	FX_DAILY,
	@Deprecated
	FX_CHARGING,

	MXFM_DAILY,

}
