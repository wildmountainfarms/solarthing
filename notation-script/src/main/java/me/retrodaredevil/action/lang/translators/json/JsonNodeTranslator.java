package me.retrodaredevil.action.lang.translators.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import me.retrodaredevil.action.lang.Argument;
import me.retrodaredevil.action.lang.ArrayArgument;
import me.retrodaredevil.action.lang.BooleanArgument;
import me.retrodaredevil.action.lang.Node;
import me.retrodaredevil.action.lang.NodeTranslator;
import me.retrodaredevil.action.lang.NumberArgument;
import me.retrodaredevil.action.lang.StringArgument;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class JsonNodeTranslator implements NodeTranslator<JsonNode> {

	private final ConfigurationProvider configurationProvider;

	public JsonNodeTranslator(ConfigurationProvider configurationProvider) {
		this.configurationProvider = requireNonNull(configurationProvider);
	}

	private JsonNode translateRacer(Node node) {
		if (!node.getNamedArguments().isEmpty()) {
			throw new IllegalArgumentException("Named arguments not supported for racer");
		}
		if (!node.getSubNodes().isEmpty()) {
			throw new IllegalArgumentException("Sub nodes not supported for racer");
		}
		List<Argument> arguments = node.getPositionalArguments();
		if (arguments.size() != 1) {
			throw new IllegalArgumentException("Invalid number of arguments for racer");
		}
		Argument argument = arguments.get(0);
		if (!(argument instanceof Node)) {
			throw new IllegalArgumentException("Argument should be a node for racer");
		}
		if (node.getLinkedNode() == null) {
			throw new IllegalArgumentException("Linked node required for racer");
		}
		Node conditionNode = (Node) argument;
		Node actionNode = node.getLinkedNode();
		return new ArrayNode(
				JsonNodeFactory.instance,
				Arrays.asList(
						translate(conditionNode),
						translate(actionNode)
				)
		);
	}

	/**
	 * Translates a data(...) node into what is basically raw JSON.
	 */
	private JsonNode translateData(Node node) {
		if (!node.getPositionalArguments().isEmpty()) {
			throw new IllegalArgumentException("data does not support positional arguments!");
		}
		if (!node.getSubNodes().isEmpty()) {
			throw new IllegalArgumentException("data does not support sub nodes!");
		}
		if (node.getLinkedNode() != null) {
			throw new IllegalArgumentException("data does not support a linked node!");
		}
		Map<String, Argument> namedArguments = node.getNamedArguments();
		Map<String, JsonNode> fieldMap = new HashMap<>();
		namedArguments.entrySet()
				.forEach(entry -> fieldMap.put(entry.getKey(), translateArgument(entry.getValue())));
		return new ObjectNode(JsonNodeFactory.instance, fieldMap);
	}

	private JsonNode translateArgument(Argument argument) {
		if (argument instanceof Node) {
			return translate((Node) argument);
		} else if (argument instanceof ArrayArgument) {
			return new ArrayNode(
					JsonNodeFactory.instance,
					((ArrayArgument) argument).getValues().stream()
							.map(this::translateArgument)
							.collect(Collectors.toList())
			);
		} else if (argument instanceof NumberArgument) {
			Number value = ((NumberArgument) argument).getValue();
			return new DecimalNode(value instanceof BigDecimal ? (BigDecimal) value : BigDecimal.valueOf(value.doubleValue()));
		} else if (argument instanceof BooleanArgument) {
			return BooleanNode.valueOf(((BooleanArgument) argument).getValue());
		} else if (argument instanceof StringArgument) {
			String rawValue = ((StringArgument) argument).getValue();
			return TextNode.valueOf(rawValue);
		} else throw new AssertionError("Unknown argument type: " + argument.getClass().getName());
	}

	@Override
	public JsonNode translate(Node node) {
		NodeConfiguration config = configurationProvider.getConfig(node.getIdentifier());
		if (config instanceof CustomNodeConfiguration) {
			if (config == CustomNodeConfiguration.RACER) {
				return translateRacer(node);
			} else if (config == CustomNodeConfiguration.DATA) {
				return translateData(node);
			} else throw new AssertionError("Unknown config: " + config);
		} else if (config instanceof SimpleNodeConfiguration) {
			SimpleNodeConfiguration simpleNodeConfiguration = (SimpleNodeConfiguration) config;
			ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
			String typeName = simpleNodeConfiguration.getIdentifierFieldValueOverride() == null
					? node.getIdentifier()
					: simpleNodeConfiguration.getIdentifierFieldValueOverride();
			objectNode.set(simpleNodeConfiguration.getIdentifierFieldKey(), new TextNode(typeName));
			List<String> positionalArgumentFieldNames = simpleNodeConfiguration.getPositionalArgumentFieldNames();
			List<Argument> positionalArguments = node.getPositionalArguments();
			if (positionalArguments.size() > positionalArgumentFieldNames.size()) {
				// We check for too many positional arguments and let whatever parses the JSON decide if there is too few arguments
				throw new IllegalArgumentException("Too many positional arguments for type: " + typeName);
			}
			for (int i = 0; i < positionalArguments.size(); i++) {
				String fieldName = positionalArgumentFieldNames.get(i);
				Argument argument = positionalArguments.get(i);
				JsonNode fieldValue = translateArgument(argument);
				objectNode.set(fieldName, fieldValue);
			}
			for (Map.Entry<String, Argument> namedArgument : node.getNamedArguments().entrySet()) {
				String rawFieldName = namedArgument.getKey();
				Argument argument = namedArgument.getValue();
				String fieldName = simpleNodeConfiguration.getNamedArgumentRenameMap().getOrDefault(rawFieldName, rawFieldName);
				if (fieldName.equals(simpleNodeConfiguration.getIdentifierFieldKey())) {
					throw new IllegalArgumentException("You cannot use the same field name as the identifier field key!");
				}
				JsonNode fieldValue = translateArgument(argument);
				objectNode.set(fieldName, fieldValue);
			}
			if (simpleNodeConfiguration.getSubNodesFieldKey() == null && !node.getSubNodes().isEmpty()) {
				throw new IllegalArgumentException("Sub nodes not supported! identifier: " + node.getIdentifier() + " subNodes: " + node.getSubNodes());
			}
			if (simpleNodeConfiguration.getSubNodesFieldKey() != null) {
				List<JsonNode> subJsonNodeList = node.getSubNodes().stream().map(this::translate).collect(Collectors.toList());
				if (!subJsonNodeList.isEmpty() || objectNode.get(simpleNodeConfiguration.getSubNodesFieldKey()) == null) {
					// only set this if we are setting it to something non-empty, or the field has nothing in it yet
					// (This prevents us from overriding something set with named args with an empty list)
					objectNode.set(simpleNodeConfiguration.getSubNodesFieldKey(), new ArrayNode(JsonNodeFactory.instance, subJsonNodeList));
				}
			}
			if (simpleNodeConfiguration.getLinkedNodeFieldKey() == null && node.getLinkedNode() != null) {
				throw new IllegalArgumentException("Linked node not supported!");
			}
			if (simpleNodeConfiguration.getLinkedNodeFieldKey() != null) {
				Node linkedNode = node.getLinkedNode();
				if (linkedNode == null) {
					objectNode.set(simpleNodeConfiguration.getLinkedNodeFieldKey(), NullNode.instance);
				} else {
					objectNode.set(simpleNodeConfiguration.getLinkedNodeFieldKey(), translate(linkedNode));
				}
			}
			return objectNode;
		} else throw new AssertionError("Unknown config type: " + config.getClass().getName());
	}
}
