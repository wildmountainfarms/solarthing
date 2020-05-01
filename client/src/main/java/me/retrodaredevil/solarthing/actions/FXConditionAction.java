package me.retrodaredevil.solarthing.actions;

import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

public class FXConditionAction {
	private final FXCondition fxCondition;
	private final PacketGroupProvider packetGroupProvider;

	public FXConditionAction(FXCondition fxCondition, PacketGroupProvider packetGroupProvider) {
		this.fxCondition = fxCondition;
		this.packetGroupProvider = packetGroupProvider;
	}

	public interface FXCondition {
		boolean isCondition(FXStatusPacket fxStatusPacket);
	}
	public interface PacketGroupProvider {
		/**
		 * @return The latest PacketCollection or null if one has not been received yet
		 */
		PacketGroup getPacketGroup();
	}
	public enum Condition {
		NO_AC,
		AC_PRESENT,
		USABLE_AC_PRESENT,

		AC_USE,
		AC_DROP,

		AUX_ON,
		AUX_OFF,

		SILENT,
		INV_ON,
		INV_OFF
	}
}
