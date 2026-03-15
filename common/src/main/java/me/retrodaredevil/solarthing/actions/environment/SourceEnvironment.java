package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.type.open.OpenSource;
import org.jspecify.annotations.NullMarked;

/**
 * Contains an {@link OpenSource} that describes who has "requested" some action and when it was requested and how it was requested
 */
@Deprecated
@NullMarked
public class SourceEnvironment {
	private final OpenSource source;

	public SourceEnvironment(OpenSource source) {
		this.source = source;
	}

	public OpenSource getSource() {
		return source;
	}
}
