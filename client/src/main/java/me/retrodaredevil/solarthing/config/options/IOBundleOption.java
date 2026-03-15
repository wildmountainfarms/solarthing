package me.retrodaredevil.solarthing.config.options;


import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

/**
 * Represents an option containing the path to a file that determines how the input and output is setup
 */
@NullMarked
public interface IOBundleOption extends ProgramOptions {
	/**
	 * @return The io file
	 */
	Path getIOBundleFilePath();
}
