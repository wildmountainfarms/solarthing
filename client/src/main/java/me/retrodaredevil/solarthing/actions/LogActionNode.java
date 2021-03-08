package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeName("log")
public class LogActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogActionNode.class);
	private final String message;
	private final boolean debug;

	public LogActionNode(
			@JsonProperty(value = "message", required = true) String message,
			@JsonProperty(value = "debug") Boolean debug
	) {
		this.message = message;
		this.debug = debug != null && debug;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		if (debug) {
			return Actions.createRunOnce(() -> LOGGER.debug(message));
		}
		return Actions.createRunOnce(() -> LOGGER.info(message));
	}
}
