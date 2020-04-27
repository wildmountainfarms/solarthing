package me.retrodaredevil.solarthing.solar.extra;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;

public enum SolarExtraPacketType implements DocumentedPacketType {
	FX_DAILY,
	FX_CHARGING,

	MXFM_DAILY,

	@Deprecated // never used, so can be removed in the future
	DAILY_UPDATE
}
