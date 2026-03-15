package me.retrodaredevil.solarthing.config.options;

import org.jspecify.annotations.NullMarked;

/**
 * Represents some sort of {@link ProgramOptions} that can contain actions that would usually
 * be executed each "iteration".
 */
@NullMarked
public interface ActionsOption extends ProgramOptions {

	/**
	 * This will not be null, but may be {@link ActionConfig#EMPTY} by default.
	 *
	 * @return The {@link ActionConfig}.
	 */
	ActionConfig getActionConfig();
}
