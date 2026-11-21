package me.retrodaredevil.action.node;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.ClockEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.InstantSource;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class WallClockActionNodeTest {

	@Test
	void testWallClockActionNodeAndWallClockAction() {
		String json = """
				{
					"type": "wallclock",
					"start": "08:30:30.500333444",
					"end": "12:05"
				}
				""";
		JsonMapper jsonMapper = JsonMapper.builder()
				.build();
		ActionNode actionNode = jsonMapper.readValue(json, ActionNode.class);
		assertInstanceOf(WallClockActionNode.class, actionNode);
		WallClockActionNode wallClockActionNode = (WallClockActionNode) actionNode;
		Instant[] time = new Instant[] { Instant.EPOCH };
		InstantSource instantSource = () -> time[0];
		ActionEnvironment actionEnvironment = new ActionEnvironment(
				new VariableEnvironment(),
				new InjectEnvironment.Builder()
						.add(new ClockEnvironment(instantSource.withZone(ZoneId.of("UTC"))))
						.build()
		);
		Action action = wallClockActionNode.createAction(actionEnvironment);

		// === act ===
		// NOTE: parsed instants below use Z to denote UTC timezone, which we define above, but that's not always a given

		assertFalse(action.isActive());

		time[0] = Instant.parse("2026-11-20T03:00:00.000Z");
		action.update();
		assertFalse(action.isDone());
		assertTrue(action.isActive());

		time[0] = Instant.parse("2026-11-20T08:30:30.500333443Z");
		action.update();
		assertFalse(action.isDone());

		time[0] = Instant.parse("2026-11-20T08:30:30.500333444Z");
		action.update();
		assertTrue(action.isDone());

		time[0] = Instant.parse("2026-11-20T12:04:00.000Z");
		action.update();
		assertTrue(action.isDone());

		time[0] = Instant.parse("2026-11-20T12:04:59.999999999Z");
		action.update();
		assertTrue(action.isDone());

		time[0] = Instant.parse("2026-11-20T12:05:00.000Z");
		action.update();
		assertFalse(action.isDone());

		time[0] = Instant.parse("2026-11-20T12:05:30.000Z");
		action.update();
		assertFalse(action.isDone());

		time[0] = Instant.parse("2026-11-20T09:00:00.000Z");
		action.update();
		assertTrue(action.isDone());

		action.end();
		assertFalse(action.isActive());

		time[0] = Instant.parse("2026-11-20T09:00:00.000Z");
		action.update();
		assertTrue(action.isDone());
		assertTrue(action.isActive());

		action.end();
		assertFalse(action.isActive());
	}
}
