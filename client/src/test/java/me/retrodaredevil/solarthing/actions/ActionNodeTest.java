package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.CallActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import me.retrodaredevil.action.node.util.NanoTimeProvider;
import me.retrodaredevil.solarthing.program.ActionUtil;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionNodeTest {
	private static final ObjectMapper MAPPER = ActionUtil.registerActionNodes(JacksonUtil.defaultMapper());

	private ActionEnvironment createEnvironment() {
		return new ActionEnvironment(
				new VariableEnvironment(),
				new InjectEnvironment.Builder()
						.add(new NanoTimeProviderEnvironment(NanoTimeProvider.SYSTEM_NANO_TIME))
						.build()
		);
	}

	@Test
	void testParse() throws JsonProcessingException {
		String json = "{ \"type\": \"call\", \"name\": \"asdf\" }";
		assertTrue(MAPPER.readValue(json, ActionNode.class) instanceof CallActionNode);
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
		ActionNode actionNode = MAPPER.readValue(json, ActionNode.class);
		Action action = actionNode.createAction(createEnvironment());
		do {
			action.update();
		} while (!action.isDone());
		action.end();
	}
}
