package me.retrodaredevil.solarthing.config.io;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.IOBundle;
import org.jspecify.annotations.NullMarked;

@JsonTypeName("standard")
@NullMarked
public class StandardIOConfig implements IOConfig {
	@Override
	public IOBundle createIOBundle() {
		return IOBundle.Defaults.STANDARD_IN_OUT;
	}
}
