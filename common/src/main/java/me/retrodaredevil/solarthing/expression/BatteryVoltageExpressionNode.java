package me.retrodaredevil.solarthing.expression;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.NumericExpression;
import me.retrodaredevil.action.node.expression.node.NumericExpressionNode;
import me.retrodaredevil.action.node.expression.result.NumericExpressionResult;
import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.actions.environment.LatestFragmentedPacketGroupEnvironment;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;

import java.util.stream.Collectors;

@JsonTypeName("battery-voltage")
public class BatteryVoltageExpressionNode implements NumericExpressionNode {
	@Override
	public NumericExpression createExpression(ActionEnvironment actionEnvironment) {
		LatestFragmentedPacketGroupEnvironment latestFragmentedPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestFragmentedPacketGroupEnvironment.class);
		FragmentedPacketGroupProvider provider = latestFragmentedPacketGroupEnvironment.getFragmentedPacketGroupProvider();

		return () -> provider.getPacketGroup().getPackets().stream()
				.filter(packet -> packet instanceof BatteryVoltage)
				.map(packet -> (BatteryVoltage) packet)
				.map(BatteryVoltage::getBatteryVoltage)
				.map(NumericExpressionResult::create)
				.collect(Collectors.toList());
	}
}
