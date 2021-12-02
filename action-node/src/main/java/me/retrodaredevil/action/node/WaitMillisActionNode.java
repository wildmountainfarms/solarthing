package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;

import java.time.Duration;

@JsonTypeName("waitms")
public class WaitMillisActionNode implements ActionNode {
	private final long wait;

	public WaitMillisActionNode(@JsonProperty("wait") long wait) {
		this.wait = wait;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		NanoTimeProviderEnvironment nanoTimeProviderEnvironment = actionEnvironment.getInjectEnvironment().get(NanoTimeProviderEnvironment.class);
		return new WaitAction(nanoTimeProviderEnvironment.getNanoTimeProvider(), Duration.ofMillis(wait));
	}
}
