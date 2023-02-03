package me.retrodaredevil.solarthing.config.options;

import java.io.File;
import java.util.List;

/**
 * Represents some sort of {@link ProgramOptions} that can contain actions that would usually
 * be executed each "iteration".
 */
public interface ActionsOption extends ProgramOptions {
	/**
	 * A legacy configuration option that currently is still supported, but will be removed in a future version
	 */
	List<File> getActionNodeFiles();

	/**
	 * This will not be null, but may be {@link ActionConfig#EMPTY} by default.
	 *
	 * @return The {@link ActionConfig}.
	 */
	ActionConfig getActionConfig();
}
