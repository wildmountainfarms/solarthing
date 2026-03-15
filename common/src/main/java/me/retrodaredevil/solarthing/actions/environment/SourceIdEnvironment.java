package me.retrodaredevil.solarthing.actions.environment;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class SourceIdEnvironment {
	private final String sourceId;

	public SourceIdEnvironment(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceId() {
		return sourceId;
	}
}
