package me.retrodaredevil.notation.translators.json;

import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@NullMarked
public class SimpleConfigurationProvider implements ConfigurationProvider {
	private final NodeConfiguration defaultConfiguration;
	private final Map<String, NodeConfiguration> configurationMap;

	public SimpleConfigurationProvider(NodeConfiguration defaultConfiguration, Map<String, NodeConfiguration> configurationMap) {
		this.defaultConfiguration = requireNonNull(defaultConfiguration);
		this.configurationMap = Collections.unmodifiableMap(new HashMap<>(configurationMap));
	}


	@Override
	public NodeConfiguration getConfig(String nodeIdentifier) {
		return configurationMap.getOrDefault(nodeIdentifier, defaultConfiguration);
	}
}
