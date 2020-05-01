package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionNodeTest {

	@Test
	void testParse() throws JsonProcessingException {
		String json = "{ \"type\": \"call\", \"name\": \"asdf\" }";
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		mapper.setInjectableValues(new InjectableValues.Std()
				.addValue("environment", new ActionEnvironment())
		);
		assertTrue(mapper.readValue(json, ActionNode.class) instanceof CallActionNode);
	}
	@Test
	void testDeclaration() throws JsonProcessingException {
		String json = "{\n" +
				"  \"type\": \"declaration\",\n" +
				"  \"main\": {\n" +
				"    \"type\": \"queue\",\n" +
				"    \"actions\": [\n" +
				"      { \"type\": \"lock\", \"name\": \"send_commands\" },\n" +
				"      { \"type\": \"call\", \"name\": \"run_commands\" },\n" +
				"      { \"type\": \"unlock\", \"name\": \"send_commands\" }\n" +
				"    ]\n" +
				"  },\n" +
				"  \"run_commands\": {\n" +
				"    \"type\": \"queue\",\n" +
				"    \"actions\": [\n" +
				"      { \"type\": \"log\", \"message\": \"Starting wait\" },\n" +
				"      { \"type\": \"log\", \"message\": \"Finished sequence!\" }\n" +
				"    ]\n" +
				"  }\n" +
				"}";
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		mapper.setInjectableValues(new InjectableValues.Std()
				.addValue("environment", new ActionEnvironment())
		);
		ActionNode actionNode = mapper.readValue(json, ActionNode.class);
		Action action = actionNode.createAction();
		do {
			action.update();
		} while (!action.isDone());
		action.end();
	}
}
