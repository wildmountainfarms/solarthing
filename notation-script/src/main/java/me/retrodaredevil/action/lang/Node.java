package me.retrodaredevil.action.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class Node implements Argument {
	private final String identifier;
	private final List<Argument> arguments;
	private final Map<String, Argument> namedArguments;
	private final List<Node> subNodes;
	private final Node linkedNode;

	public Node(String identifier, List<Argument> arguments, Map<String, Argument> namedArguments, List<Node> subNodes, Node linkedNode) {
		this.identifier = requireNonNull(identifier);
		this.arguments = Collections.unmodifiableList(new ArrayList<>(arguments));
		this.namedArguments = Collections.unmodifiableMap(new LinkedHashMap<>(namedArguments));
		this.subNodes = Collections.unmodifiableList(new ArrayList<>(subNodes));
		this.linkedNode = linkedNode; // may be null
	}

	public String getIdentifier() {
		return identifier;
	}
	public List<Argument> getPositionalArguments() {
		return arguments;
	}
	/** @return An ordered map of names to arguments*/
	public Map<String, Argument> getNamedArguments() {
		return namedArguments;
	}
	public List<Node> getSubNodes() {
		return subNodes;
	}
	/** @return The linked node or null if not present*/
	public Node getLinkedNode() {
		return linkedNode;
	}
}
