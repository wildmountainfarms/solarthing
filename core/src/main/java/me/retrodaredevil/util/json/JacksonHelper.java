package me.retrodaredevil.util.json;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class JacksonHelper {
	private JacksonHelper(){ throw new UnsupportedOperationException(); }

	public static JsonNode require(DeserializationContext context, JsonNode parentNode, String childName) throws JsonMappingException {
		JsonNode node = parentNode.get(childName);
		if(node == null){
			throw MismatchedInputException.from(context.getParser(), context.getContextualType(), "childName='" + childName + "' was not present! parentNode=" + parentNode);
		}
		return node;
	}
	public static JsonNode require(DeserializationContext context, JsonNode parentNode, String childName, AssertionProvider assertionProvider) throws JsonMappingException {
		JsonNode node = require(context, parentNode, childName);
		boolean isTrue = assertionProvider.getMustBeTrue(node);
		if(!isTrue){
			throw MismatchedInputException.from(context.getParser(), context.getContextualType(), "childName='" + childName + "' node=" + node + ". Assertion failed.");
		}
		return node;
	}
	public interface AssertionProvider {
		boolean getMustBeTrue(JsonNode node);
	}
}
