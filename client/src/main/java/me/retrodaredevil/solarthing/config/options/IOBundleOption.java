package me.retrodaredevil.solarthing.config.options;


import java.nio.file.Path;

/**
 * Represents an option containing the path to a file that determines how the input and output is setup
 */
public interface IOBundleOption extends ProgramOptions {
	/**
	 * @return The io file
	 */
	Path getIOBundleFilePath();
}
