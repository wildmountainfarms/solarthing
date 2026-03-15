package me.retrodaredevil.solarthing.config.io;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.IOBundle;
import org.jspecify.annotations.NullMarked;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;

@JsonTypeName("blank")
@NullMarked
public class BlankIOConfig implements IOConfig {
	@Override
	public IOBundle createIOBundle() throws Exception {
		return IOBundle.of(new ByteArrayInputStream(new byte[0]), new OutputStream() {
			@Override
			public void write(int i) {
			}
		});
	}
}
