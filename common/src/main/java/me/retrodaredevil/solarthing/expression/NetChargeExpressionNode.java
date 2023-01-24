package me.retrodaredevil.solarthing.expression;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.NumericExpression;
import me.retrodaredevil.action.node.expression.node.ExpressionNode;
import me.retrodaredevil.action.node.expression.result.NumericExpressionResult;
import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.actions.environment.LatestFragmentedPacketGroupEnvironment;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.common.BasicChargeController;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

import java.util.Collections;

@JsonTypeName("net-charge")
public class NetChargeExpressionNode implements ExpressionNode {
	@Override
	public NumericExpression createExpression(ActionEnvironment actionEnvironment) {
		LatestFragmentedPacketGroupEnvironment latestFragmentedPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestFragmentedPacketGroupEnvironment.class);
		FragmentedPacketGroupProvider provider = latestFragmentedPacketGroupEnvironment.getFragmentedPacketGroupProvider();

		return () -> {
			FragmentedPacketGroup packetGroup = provider.getPacketGroup();
			if (packetGroup == null) {
				return Collections.emptyList();
			}
			double totalWatts = 0;
			for (Packet packet : packetGroup.getPackets()) {
				if (packet instanceof FXStatusPacket) {
					totalWatts -= ((FXStatusPacket) packet).getInverterWattage();
				} else if (packet instanceof BasicChargeController) {
					totalWatts += ((BasicChargeController) packet).getChargingPower().doubleValue();
				}
			}
			return Collections.singletonList(NumericExpressionResult.create(totalWatts));
		};
	}
}
