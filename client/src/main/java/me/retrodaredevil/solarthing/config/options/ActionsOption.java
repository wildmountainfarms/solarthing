package me.retrodaredevil.solarthing.config.options;

import java.io.File;
import java.util.List;

/**
 * Represents some sort of {@link ProgramOptions} that can contain actions that would usually
 * be executed each "iteration".
 */
public interface ActionsOption extends ProgramOptions {
	List<File> getActionNodeFiles();
}
