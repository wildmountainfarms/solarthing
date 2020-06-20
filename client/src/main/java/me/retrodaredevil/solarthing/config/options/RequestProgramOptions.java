package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("request")
public class RequestProgramOptions extends RequestProgramOptionsBase{
	@Override
	public ProgramType getProgramType() {
		return ProgramType.REQUEST;
	}

}
