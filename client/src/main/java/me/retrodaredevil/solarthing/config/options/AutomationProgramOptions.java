package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("automation")
@JsonExplicit
public class AutomationProgramOptions extends DatabaseTimeZoneOptionBase implements ActionsOption {
	@JsonProperty("actions")
	private List<File> actionNodeFiles = Collections.emptyList();
	@JsonProperty("action_config")
	private ActionConfig actionConfig = ActionConfig.EMPTY;

	@JsonProperty("period")
	private String periodDurationString = "PT5S";

	@Override
	public ProgramType getProgramType() {
		return ProgramType.AUTOMATION;
	}

	@Override
	public List<File> getActionNodeFiles() {
		return requireNonNull(actionNodeFiles, "You cannot supply a null value here!");
	}

	@Override
	public ActionConfig getActionConfig() {
		return requireNonNull(actionConfig);
	}

	public long getPeriodMillis() {
		return Duration.parse(periodDurationString).toMillis();
	}
}
