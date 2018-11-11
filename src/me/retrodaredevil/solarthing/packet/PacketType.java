package me.retrodaredevil.solarthing.packet;

/**
 * Represents the packet type.
 *
 * NOTE: In the future, do NOT override the {@link #toString()} method. Doing so may break code
 * from other projects
 */
public enum PacketType {
	FX_STATUS, MXFM_STATUS, FLEXNET_DC_STATUS
}
