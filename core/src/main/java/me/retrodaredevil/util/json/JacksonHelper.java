package me.retrodaredevil.util.json;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.MismatchedInputException;

@UtilityClass
@NullMarked
public final class JacksonHelper {
	private JacksonHelper(){ throw new UnsupportedOperationException(); }

	/**
	 * @throws MismatchedInputException when unable to get {@code childName} from {@code parentNode}
	 */
	public static JsonNode require(DeserializationContext context, JsonNode parentNode, String childName) {
		JsonNode node = parentNode.get(childName);
		if(node == null){
			throw MismatchedInputException.from(context.getParser(), context.getContextualType(), "childName='" + childName + "' was not present! parentNode=" + parentNode);
		}
		return node;
	}
	/**
	 * @throws MismatchedInputException when the {@code assertionProvider} does not return true after being passed the {@code childName} node from {@code parentNode}
	 */
	public static JsonNode require(DeserializationContext context, JsonNode parentNode, String childName, AssertionProvider assertionProvider) {
		JsonNode node = require(context, parentNode, childName);
		boolean isTrue = assertionProvider.getMustBeTrue(node);
		if(!isTrue){
			throw MismatchedInputException.from(context.getParser(), context.getContextualType(), "childName='" + childName + "' node=" + node + ". Assertion failed.");
		}
		return node;
	}
	// TODO consider removing interface and using Predicate<JsonNode>
	public interface AssertionProvider {
		boolean getMustBeTrue(JsonNode node);
	}
}
