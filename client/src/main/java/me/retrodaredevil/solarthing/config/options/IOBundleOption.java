package me.retrodaredevil.solarthing.config.options;


import java.io.File;

/**
 * Represents an option containing the path to a file that determines how the input and output is setup
 */
public interface IOBundleOption extends ProgramOptions {
	/**
	 * @return The io file. May throw an exception in some cases (for instance if {@link RoverOption#getDummyFile()} != null)
	 */
	File getIOBundleFile();
}
