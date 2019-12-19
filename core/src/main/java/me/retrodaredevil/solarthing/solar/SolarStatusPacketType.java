package me.retrodaredevil.solarthing.solar;

import me.retrodaredevil.solarthing.packets.DocumentedPacketType;

/**
 * Represents the packet type.
 * <p>
 * NOTE: In the future, do NOT override the {@link #toString()} method. Doing so may break code
 * from other projects
 * <p>
 * NOTE: This enum may have more values added to it in the future
 */
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
}
