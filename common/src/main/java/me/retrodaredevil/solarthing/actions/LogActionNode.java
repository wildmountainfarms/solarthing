package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.SolarThingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeName("log")
public class LogActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogActionNode.class);
	private final String message;
	private final boolean debug;
	private final boolean summary;

	public LogActionNode(
			@JsonProperty(value = "message", required = true) String message,
			@JsonProperty("debug") Boolean debug,
			@JsonProperty("summary") Boolean summary) {
		this.message = message;
		this.debug = debug != null && debug;
		this.summary = summary != null && summary;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		if (summary) {
			if (debug) {
				return Actions.createRunOnce(() -> LOGGER.debug(SolarThingConstants.SUMMARY_MARKER, message));
			}
			return Actions.createRunOnce(() -> LOGGER.info(SolarThingConstants.SUMMARY_MARKER, message));
		}

		if (debug) {
			return Actions.createRunOnce(() -> LOGGER.debug(message));
		}
		return Actions.createRunOnce(() -> LOGGER.info(message));
	}
}
