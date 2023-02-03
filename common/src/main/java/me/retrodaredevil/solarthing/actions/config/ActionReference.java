package me.retrodaredevil.solarthing.actions.config;

import java.io.File;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Holds data necessary to construct an {@link me.retrodaredevil.action.node.ActionNode} such as the file and the format/language of this file.
 */
public final class ActionReference {
	private final Path path;
	private final ActionFormat format;

	public ActionReference(Path path, ActionFormat format) {
		this.path = requireNonNull(path);
		this.format = requireNonNull(format);
	}

	@Deprecated
	public File getFile() {
		return path.toFile();
	}
	public Path getPath() {
		return path;
	}

	public ActionFormat getFormat() {
		return format;
	}
}
