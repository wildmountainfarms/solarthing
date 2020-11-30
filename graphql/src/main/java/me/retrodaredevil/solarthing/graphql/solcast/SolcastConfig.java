package me.retrodaredevil.solarthing.graphql.solcast;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(builder = SolcastConfig.Builder.class)
public class SolcastConfig {
	private final Map<String, Entry> sourceEntryMap;

	public SolcastConfig(Map<String, Entry> sourceEntryMap) {
		this.sourceEntryMap = sourceEntryMap;
	}
	public @Nullable Entry getEntry(String sourceId) {
		return sourceEntryMap.get(sourceId);
	}
	public Collection<String> getSources() {
		return sourceEntryMap.keySet();
	}

	public static final class Entry {
		private final String resourceId;
		private final String apiKey;

		@JsonCreator
		public Entry(@JsonProperty(value = "resource", required = true) String resourceId, @JsonProperty(value = "key", required = true) String apiKey) {
			requireNonNull(this.resourceId = resourceId);
			requireNonNull(this.apiKey = apiKey);
		}

		public String getResourceId() {
			return resourceId;
		}

		public String getApiKey() {
			return apiKey;
		}
	}
	static class Builder {
		private final Map<String, Entry> sourceEntryMap = new HashMap<>();
		@JsonAnySetter
		public void set(String source, Entry entry) {
			sourceEntryMap.put(source, entry);
		}
		SolcastConfig build() {
			return new SolcastConfig(new HashMap<>(sourceEntryMap));
		}
	}
}
