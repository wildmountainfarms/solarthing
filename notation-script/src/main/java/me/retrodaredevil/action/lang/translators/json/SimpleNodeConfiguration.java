package me.retrodaredevil.action.lang.translators.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class SimpleNodeConfiguration implements NodeConfiguration {
	private final String identifierFieldKey;
	private final List<String> positionalArgumentFieldNames;
	private final Map<String, String> namedArgumentRenameMap;
	private final String subNodesFieldKey;
	private final String linkedNodeFieldKey;

	public SimpleNodeConfiguration(String identifierFieldKey, List<String> positionalArgumentFieldNames, Map<String, String> namedArgumentRenameMap, String subNodesFieldKey, String linkedNodeFieldKey) {
		if (positionalArgumentFieldNames.contains(identifierFieldKey)) {
			throw new IllegalArgumentException("The identifierFieldKey cannot be in positionalArgumentFieldNames");
		}
		if (namedArgumentRenameMap.containsValue(identifierFieldKey)) {
			throw new IllegalArgumentException("The identifierFieldKey cannot be a value in namedArgumentRenameMap");
		}
		this.identifierFieldKey = requireNonNull(identifierFieldKey);
		this.positionalArgumentFieldNames = Collections.unmodifiableList(new ArrayList<>(positionalArgumentFieldNames));
		this.namedArgumentRenameMap = Collections.unmodifiableMap(new HashMap<>(namedArgumentRenameMap));
		this.subNodesFieldKey = subNodesFieldKey;
		this.linkedNodeFieldKey = linkedNodeFieldKey;

	}

	public String getIdentifierFieldKey() {
		return identifierFieldKey;
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
}
