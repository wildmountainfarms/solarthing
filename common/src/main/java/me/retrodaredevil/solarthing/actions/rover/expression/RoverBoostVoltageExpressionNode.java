package me.retrodaredevil.solarthing.actions.rover.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.expression.NumericExpression;
import me.retrodaredevil.action.node.expression.node.ExpressionNode;
import me.retrodaredevil.action.node.expression.result.NumericExpressionResult;
import me.retrodaredevil.solarthing.actions.rover.RoverMatcher;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;

@JsonTypeName("rover-boost-voltage")
@NullMarked
public class RoverBoostVoltageExpressionNode implements ExpressionNode {
	private final RoverMatcher roverMatcher;

	@JsonCreator
	public RoverBoostVoltageExpressionNode(
			@JsonProperty("fragment") @Nullable Integer fragmentId,
			@JsonProperty("number") @Nullable Integer number
	) {
		roverMatcher = RoverMatcher.createFromRaw(fragmentId, number);
	}
	@Override
	public NumericExpression createExpression(ActionEnvironment actionEnvironment) {
		RoverMatcher.Provider provider = roverMatcher.createProvider(actionEnvironment.getInjectEnvironment());
		return () -> {
			RoverStatusPacket packet = provider.get();
			return packet == null
					? Collections.emptyList()
					: Collections.singletonList(NumericExpressionResult.create(packet.getBoostChargingVoltageRaw()));
		};
	}
}
