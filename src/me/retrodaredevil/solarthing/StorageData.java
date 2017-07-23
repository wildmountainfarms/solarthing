package me.retrodaredevil.solarthing;

import java.util.List;

import me.retrodaredevil.solarthing.packet.FXStatusPacket;
import me.retrodaredevil.solarthing.packet.MXFMStatusPacket;

public class StorageData {

	private List<FXStatusPacket> fxPackets;
	private List<MXFMStatusPacket> mxfmPackets;
	
	public StorageData(List<FXStatusPacket> fxPackets, List<MXFMStatusPacket> mxfmPackets){
		this.fxPackets = fxPackets;
		this.mxfmPackets = mxfmPackets;
	}
	
	public List<FXStatusPacket> getFXPackets(){
		return fxPackets;
	}
	public List<MXFMStatusPacket> getMXFMPackets(){
		return mxfmPackets;
	}
	
}
