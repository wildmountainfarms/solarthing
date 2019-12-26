package me.retrodaredevil.solarthing.config.io;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.IOBundle;

@JsonTypeName("standard")
public class StandardIOConfig implements IOConfig {
	@Override
	public IOBundle createIOBundle() {
		return IOBundle.Defaults.STANDARD_IN_OUT;
	}
}
