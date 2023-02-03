package me.retrodaredevil.action.lang.translators.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class SimpleNodeConfiguration implements NodeConfiguration {
	private final String identifierFieldKey;
	private final String identifierFieldValueOverride;
	private final List<String> positionalArgumentFieldNames;
	private final Map<String, String> namedArgumentRenameMap;
	private final String subNodesFieldKey;
	private final String linkedNodeFieldKey;

	public SimpleNodeConfiguration(String identifierFieldKey, String identifierFieldValueOverride, List<String> positionalArgumentFieldNames, Map<String, String> namedArgumentRenameMap, String subNodesFieldKey, String linkedNodeFieldKey) {
		if (positionalArgumentFieldNames.contains(identifierFieldKey)) {
			throw new IllegalArgumentException("The identifierFieldKey cannot be in positionalArgumentFieldNames");
		}
		if (namedArgumentRenameMap.containsValue(identifierFieldKey)) {
			throw new IllegalArgumentException("The identifierFieldKey cannot be a value in namedArgumentRenameMap");
		}
		this.identifierFieldKey = requireNonNull(identifierFieldKey);
		this.identifierFieldValueOverride = identifierFieldValueOverride;
		this.positionalArgumentFieldNames = Collections.unmodifiableList(new ArrayList<>(positionalArgumentFieldNames));
		this.namedArgumentRenameMap = Collections.unmodifiableMap(new HashMap<>(namedArgumentRenameMap));
		this.subNodesFieldKey = subNodesFieldKey;
		this.linkedNodeFieldKey = linkedNodeFieldKey;

	}
	public static Builder builder() {
		return new Builder();
	}

	public String getIdentifierFieldKey() {
		return identifierFieldKey;
	}
	public String getIdentifierFieldValueOverride() {
		return identifierFieldValueOverride;
	}

	public List<String> getPositionalArgumentFieldNames() {
		return positionalArgumentFieldNames;
	}

	public Map<String, String> getNamedArgumentRenameMap() {
		return namedArgumentRenameMap;
	}

	public String getSubNodesFieldKey() {
		return subNodesFieldKey;
	}

	public String getLinkedNodeFieldKey() {
		return linkedNodeFieldKey;
	}

	public static final class Builder {
		private String identifierFieldKey = "type";
		private String identifierFieldValueOverride = null;
		private final List<String> positionalArgumentFieldNames = new ArrayList<>();
		private final Map<String, String> namedArgumentRenameMap = new HashMap<>();
		private String subNodesFieldKey = null;
		private String linkedNodeFieldKey = null;

		public Builder copy() {
			Builder newBuilder = new Builder();
			newBuilder.identifierFieldKey = identifierFieldKey;
			newBuilder.identifierFieldValueOverride = identifierFieldValueOverride;
			newBuilder.positionalArgumentFieldNames.addAll(positionalArgumentFieldNames);
			newBuilder.namedArgumentRenameMap.putAll(namedArgumentRenameMap);
			newBuilder.subNodesFieldKey = subNodesFieldKey;
			newBuilder.linkedNodeFieldKey = linkedNodeFieldKey;
			return newBuilder;
		}

		public Builder identifierFieldKey(String identifierFieldKey) {
			this.identifierFieldKey = requireNonNull(identifierFieldKey);
			return this;
		}
		public Builder rename(String identifierFieldValueOverride) {
			this.identifierFieldValueOverride = identifierFieldValueOverride;
			return this;
		}
		public Builder args(String... positionalArguments) {
			positionalArgumentFieldNames.addAll(Arrays.asList(positionalArguments));
			return this;
		}
		public Builder argRename(String sourceNamedArgument, String resultNamedArgument) {
			namedArgumentRenameMap.put(sourceNamedArgument, resultNamedArgument);
			return this;
		}
		public Builder subNodes(String subNodesFieldKey) {
			this.subNodesFieldKey = subNodesFieldKey;
			return this;
		}
		public Builder linkedNode(String linkedNodeFieldKey) {
			this.linkedNodeFieldKey = linkedNodeFieldKey;
			return this;
		}
		public SimpleNodeConfiguration build() {
			return new SimpleNodeConfiguration(identifierFieldKey, identifierFieldValueOverride, positionalArgumentFieldNames, namedArgumentRenameMap, subNodesFieldKey, linkedNodeFieldKey);
		}
	}
}
