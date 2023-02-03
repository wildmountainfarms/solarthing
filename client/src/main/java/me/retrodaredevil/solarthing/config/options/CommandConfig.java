package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.config.ActionFormat;
import me.retrodaredevil.solarthing.actions.config.ActionReference;
import me.retrodaredevil.solarthing.commands.CommandInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class CommandConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandConfig.class);
	private final CommandInfo commandInfo;
	private final ActionReference actionReference;

	@JsonCreator
	public CommandConfig(
			@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "display_name", required = true) String displayName,
			@JsonProperty(value = "description", required = true) String description,
			@JsonProperty(value = "action", required = true) Path actionPath,
			@JsonProperty("format") ActionFormat format) {
		this.commandInfo = new CommandInfo(name, displayName, description);
		this.actionReference = new ActionReference(actionPath, inferFormat(actionPath, format));
	}
	private static ActionFormat inferFormat(Path actionPath, ActionFormat format) {
		if (format == null) {
			// This logic will allow us to default to NOTATION_SCRIPT, but to still support legacy configuration files
			// We should be able to remove this logic in the future.
			if (actionPath.getFileName().toString().endsWith(".json")) {
				LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "(Deprecated) Implicitly inferring format=RAW_JSON for action path: '" + actionPath + "'. Please explicitly define format! Implicit definition for RAW_JSON is deprecated and will eventually be removed.");
				return ActionFormat.RAW_JSON;
			}
			return ActionFormat.NOTATION_SCRIPT;
		}
		return format;
	}

	public CommandInfo getCommandInfo() {
		return commandInfo;
	}

	public ActionReference getActionReference() {
		return actionReference;
	}
}
