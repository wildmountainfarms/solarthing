package me.retrodaredevil.solarthing.packet.mxfm;

public interface MXFMStatusPacket extends MXFMStatusPacketRaw{
	String getAuxModeName();
	String getErrorsString();
	String getChargerModeName();
}
