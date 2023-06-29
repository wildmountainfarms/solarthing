package me.retrodaredevil.solarthing.rest.spring;

import java.beans.PropertyEditorSupport;
import java.nio.file.Path;

public class NioPathPropertyEditorSupport extends PropertyEditorSupport {
	@Override
	public String getAsText() {
		Path path = (Path) getValue();
		return path.toString();
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(Path.of(text));
	}
}
