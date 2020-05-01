package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeName("log")
public class LogActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogActionNode.class);
	private final String message;

	public LogActionNode(@JsonProperty("message") String message) {
		this.message = message;
	}

	@Override
	public Action createAction() {
		return Actions.createRunOnce(() -> LOGGER.info(message));
	}
}
