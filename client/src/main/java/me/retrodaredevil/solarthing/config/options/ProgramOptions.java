package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

@JsonSubTypes({
		@JsonSubTypes.Type(MateProgramOptions.class),
		@JsonSubTypes.Type(RoverProgramOptions.class),
		@JsonSubTypes.Type(RoverSetupProgramOptions.class),
		@JsonSubTypes.Type(PVOutputUploadProgramOptions.class),
		@JsonSubTypes.Type(RequestProgramOptions.class),
		@JsonSubTypes.Type(AutomationProgramOptions.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonExplicit
public interface ProgramOptions {
	ProgramType getProgramType();
}
