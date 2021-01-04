package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.actions.environment.VariableEnvironment;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionNodeTest {

	private ActionEnvironment createEnvironment() {
		return new ActionEnvironment(new VariableEnvironment(), new VariableEnvironment(), new InjectEnvironment.Builder().build());
	}

	@Test
	void testParse() throws JsonProcessingException {
		String json = "{ \"type\": \"call\", \"name\": \"asdf\" }";
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		assertTrue(mapper.readValue(json, ActionNode.class) instanceof CallActionNode);
	}
	@Test
	void testDeclaration() throws JsonProcessingException {
		String json = "{\n" +
				"  \"type\": \"race\",\n" +
				"  \"racers\": [\n" +
				"    [{ \"type\": \"waitms\", \"wait\": 500}, { \"type\": \"log\", \"message\": \"500ms finished first!\"}],\n" +
				"    [{ \"type\": \"waitms\", \"wait\": 200}, { \"type\": \"log\", \"message\": \"200ms finished first!\"}],\n" +
				"    [{ \"type\": \"waitms\", \"wait\": 1000}, { \"type\": \"log\", \"message\": \"1000ms finished first!\"}]\n" +
				"  ]\n" +
				"}";
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		ActionNode actionNode = mapper.readValue(json, ActionNode.class);
		Action action = actionNode.createAction(createEnvironment());
		do {
			action.update();
		} while (!action.isDone());
		action.end();
	}
}
