package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("CanBeFinal")
@JsonTypeName("automation")
@JsonExplicit
public class AutomationProgramOptions extends DatabaseTimeZoneOptionBase implements ActionsOption {
	private static final Logger LOGGER = LoggerFactory.getLogger(AutomationProgramOptions.class);

	@JsonProperty("action_config")
	private ActionConfig actionConfig = ActionConfig.EMPTY;

	@JsonProperty("period")
	private String periodDurationString = "PT5S";

	@Override
	public ProgramType getProgramType() {
		return ProgramType.AUTOMATION;
	}

	@Override
	public ActionConfig getActionConfig() {
		return requireNonNull(actionConfig);
	}

	public long getPeriodMillis() {
		return Duration.parse(periodDurationString).toMillis();
	}

	@JsonSetter("actions")
	private void setActionNodeFiles(List<String> actionNodeFiles) {
		LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Deprecated) Please use action_config configuration instead of actions!");
		// TODO use a different exception here
		throw new RuntimeException("Migration required! You must now use action_config instead of actions to configure actions!");
	}
}
