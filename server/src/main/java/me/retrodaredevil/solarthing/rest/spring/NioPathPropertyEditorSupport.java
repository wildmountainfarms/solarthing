package me.retrodaredevil.solarthing.rest.spring;

import java.beans.PropertyEditorSupport;
import java.nio.file.Path;

/**
 * This class allows spring configuration to read in string values as {@link Path}s. For example:
 * <pre>
 * {@code
 * @Value("${solarthing.config.database}")
 * private Path databaseFile;
 * }
 * </pre>
 */
public class NioPathPropertyEditorSupport extends PropertyEditorSupport {
	@Override
	public String getAsText() {
		Path path = (Path) getValue();
		return path.toString();
	}

	@Override
	public void setAsText(String text) {
		setValue(Path.of(text));
	}
}
