package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({
		@JsonSubTypes.Type(MateProgramOptions.class),
		@JsonSubTypes.Type(RoverProgramOptions.class),
		@JsonSubTypes.Type(RoverSetupProgramOptions.class),
		@JsonSubTypes.Type(PVOutputUploadProgramOptions.class),
		@JsonSubTypes.Type(MessageSenderProgramOptions.class),
		@JsonSubTypes.Type(RequestProgramOptions.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface ProgramOptions {
	ProgramType getProgramType();
}
