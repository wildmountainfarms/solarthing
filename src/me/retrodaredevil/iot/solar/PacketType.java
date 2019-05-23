package me.retrodaredevil.iot.solar;

/**
 * Represents the packet type.
 *
 * NOTE: In the future, do NOT override the {@link #toString()} method. Doing so may break code
 * from other projects
 */
public enum PacketType {
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
	FLEXNET_DC_STATUS
}
