package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jspecify.annotations.NullMarked;

@JsonTypeName("request")
@NullMarked
public class RequestProgramOptions extends RequestProgramOptionsBase{
	@Override
	public ProgramType getProgramType() {
		return ProgramType.REQUEST;
	}

}
