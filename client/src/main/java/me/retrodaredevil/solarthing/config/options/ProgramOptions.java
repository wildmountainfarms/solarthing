package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({
		@JsonSubTypes.Type(MateProgramOptions.class),
		@JsonSubTypes.Type(RoverProgramOptions.class),
		@JsonSubTypes.Type(RoverSetupProgramOptions.class),
		@JsonSubTypes.Type(PVOutputUploadProgramOptions.class),
		@JsonSubTypes.Type(MattermostProgramOptions.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface ProgramOptions {
	ProgramType getProgramType();
}
