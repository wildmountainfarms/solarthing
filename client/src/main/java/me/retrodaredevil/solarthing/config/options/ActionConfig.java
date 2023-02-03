package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.actions.config.ActionFormat;
import me.retrodaredevil.solarthing.actions.config.ActionReference;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class ActionConfig {
	public static final ActionConfig EMPTY = new ActionConfig(Collections.emptyList());
	private final List<Entry> entries;

	@JsonCreator
	public ActionConfig(@JsonProperty(value = "entries", required = true) List<Entry> entries) {
		this.entries = requireNonNull(entries);
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public static final class Entry {
		private final ActionReference actionReference;
		private final boolean runOnce;

		@JsonCreator
		public Entry(
				@JsonProperty(value = "path", required = true) Path path,
				@JsonProperty("format") ActionFormat format,
				@JsonProperty("once") Boolean runOnce
		) {
			this.actionReference = new ActionReference(
					path,
					format == null ? ActionFormat.NOTATION_SCRIPT : format
			);
			this.runOnce = Boolean.TRUE.equals(runOnce); // by default false
		}

		public ActionReference getActionReference() {
			return actionReference;
		}

		public boolean isRunOnce() {
			return runOnce;
		}
	}
}
