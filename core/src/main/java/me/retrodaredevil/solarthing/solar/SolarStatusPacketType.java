package me.retrodaredevil.solarthing.solar;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.jspecify.annotations.NullMarked;

/**
 * Represents the packet type.
 * <p>
 * NOTE: In the future, do NOT override the {@link #toString()} method. Doing so may break code
 * from other projects
 * <p>
 * NOTE: This enum may have more values added to it in the future
 */
@NullMarked
public enum SolarStatusPacketType implements DocumentedPacketType {
	/**
	 * FX Status Packets
	 */
	FX_STATUS,
	/**
	 * MX/FM Status Packets
	 */
	MXFM_STATUS,
	/**
	 * FlexNET DC Status Packets
	 */
	FLEXNET_DC_STATUS,
	/**
	 * Renogy Rover Charge Controller Status Packet
	 */
	RENOGY_ROVER_STATUS,

	BATTERY_VOLTAGE_ONLY,
	PZEM_SHUNT,
	TRACER_STATUS,
}
