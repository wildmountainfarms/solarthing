package me.retrodaredevil.solarthing.packets.instance;

public interface InstanceSourcePacket extends InstancePacket {
	String DEFAULT_SOURCE_ID = "default";
	
	/**
	 * Should be serialized as "sourceId"
	 * @return
	 */
	String getSourceId();
}
