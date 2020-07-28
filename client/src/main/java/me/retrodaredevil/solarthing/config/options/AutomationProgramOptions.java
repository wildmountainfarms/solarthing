package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.io.File;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("automation")
@JsonExplicit
public class AutomationProgramOptions extends DatabaseTimeZoneOptionBase {
	@JsonProperty("actions")
	private List<File> actionNodeFiles;

	@Override
	public ProgramType getProgramType() {
		return ProgramType.AUTOMATION;
	}

	public List<File> getActionNodeFiles() {
		return requireNonNull(actionNodeFiles);
	}
}
