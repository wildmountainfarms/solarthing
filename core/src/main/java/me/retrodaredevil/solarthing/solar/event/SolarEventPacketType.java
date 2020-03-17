package me.retrodaredevil.solarthing.solar.event;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;

public enum SolarEventPacketType implements DocumentedPacketType {
	FX_OPERATIONAL_MODE_CHANGE,
	FX_AC_MODE_CHANGE,
	FX_AUX_STATE_CHANGE,
	FX_DAILY_DAY_END,
	FX_ERROR_MODE_CHANGE,

	MXFM_CHARGER_MODE_CHANGE,
	MXFM_AUX_MODE_CHANGE,
	MXFM_DAILY_DAY_END,
	MXFM_RAW_DAY_END,
	MXFM_ERROR_MODE_CHANGE
}
