package me.retrodaredevil.solarthing.packets.instance;

public interface InstanceTargetPacket extends InstancePacket {
	/**
	 * Should be serialized as "targetId"
	 * @return
	 */
	String getTargetId();
}
