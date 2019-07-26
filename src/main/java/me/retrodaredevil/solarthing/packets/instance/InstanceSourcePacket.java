package me.retrodaredevil.solarthing.packets.instance;

public interface InstanceSourcePacket extends InstancePacket {
	/**
	 * Should be serialized as "sourceId"
	 * @return
	 */
	String getSourceId();
}
