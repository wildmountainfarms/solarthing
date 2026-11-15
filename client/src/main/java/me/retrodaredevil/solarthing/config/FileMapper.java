package me.retrodaredevil.solarthing.config;

import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

@NullMarked
public interface FileMapper {
	Path map(String fileName);

	String JACKSON_INJECT_IDENTIFIER = "fileMapperJacksonInject";
	FileMapper ONE_TO_ONE = Path::of;
}
