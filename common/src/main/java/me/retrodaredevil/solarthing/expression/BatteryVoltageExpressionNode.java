package me.retrodaredevil.solarthing.expression;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.NumericExpression;
import me.retrodaredevil.action.node.expression.node.ExpressionNode;
import me.retrodaredevil.action.node.expression.result.NumericExpressionResult;
import me.retrodaredevil.solarthing.PacketGroupProvider;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;

import java.util.stream.Collectors;

@JsonTypeName("battery-voltage")
public class BatteryVoltageExpressionNode implements ExpressionNode {
	@Override
	public NumericExpression createExpression(ActionEnvironment actionEnvironment) {
		LatestPacketGroupEnvironment latestFragmentedPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestPacketGroupEnvironment.class);
		PacketGroupProvider provider = latestFragmentedPacketGroupEnvironment.getPacketGroupProvider();

		return () -> provider.getPacketGroup().getPackets().stream()
				.filter(packet -> packet instanceof BatteryVoltage)
				.map(packet -> (BatteryVoltage) packet)
				.map(BatteryVoltage::getBatteryVoltage)
				.map(NumericExpressionResult::create)
				.collect(Collectors.toList());
	}
}
