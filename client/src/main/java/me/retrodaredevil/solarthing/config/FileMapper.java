package me.retrodaredevil.solarthing.config;

import java.io.File;

public interface FileMapper {
	File map(String fileName);

	String JACKSON_INJECT_IDENTIFIER = "fileMapperJacksonInject";
	FileMapper ONE_TO_ONE = File::new;

	class ParentDirectory implements FileMapper {
		private final File parentDirectory;

		public ParentDirectory(File parentDirectory) {
			this.parentDirectory = parentDirectory;
		}

		@Override
		public File map(String fileName) {
			return new File(parentDirectory, fileName);
		}
	}
}
