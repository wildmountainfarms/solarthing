package me.retrodaredevil.solarthing.type.closed.meta;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum TargetedMetaPacketType implements DocumentedPacketType {
	DEVICE_INFO,
	DATA_INFO,
	FX_CHARGING_SETTINGS,
	FX_CHARGING_TEMPERATURE_ADJUST,
}
