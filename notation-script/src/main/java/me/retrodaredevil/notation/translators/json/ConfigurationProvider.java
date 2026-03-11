package me.retrodaredevil.notation.translators.json;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ConfigurationProvider {
	NodeConfiguration getConfig(String nodeIdentifier);
}
