package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.open.OpenSource;

/**
 * Contains an {@link OpenSource} that describes who has "requested" some action and when it was requested and how it was requested
 */
public class SourceEnvironment {
	private final OpenSource source;

	public SourceEnvironment(OpenSource source) {
		this.source = source;
	}

	public OpenSource getSource() {
		return source;
	}
}
