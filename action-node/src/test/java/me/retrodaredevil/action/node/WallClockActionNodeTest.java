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

	/**
	 * Tests deserialization of a {@link WallClockActionNode},
	 * tests exact logic for what times are considered in range,
	 * tests {@link Action#isActive()} behavior,
	 * ensures that ending the action and starting it again works.
	 */
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

	@Test
	void testOverlappingRangeWithDaysOfTheWeek() {
		// Sunday night/Monday morning
		String json = """
				{
					"type": "wallclock",
					"start": "23:00",
					"end": "03:00",
					"days": ["SUNDAY"]
				}
				""";
		JsonMapper jsonMapper = JsonMapper.builder()
				.build();
		ActionNode actionNode = jsonMapper.readValue(json, ActionNode.class);

		Instant[] time = new Instant[] { Instant.EPOCH };
		InstantSource instantSource = () -> time[0];
		ActionEnvironment actionEnvironment = new ActionEnvironment(
				new VariableEnvironment(),
				new InjectEnvironment.Builder()
						.add(new ClockEnvironment(instantSource.withZone(ZoneId.of("UTC"))))
						.build()
		);
		Action action = actionNode.createAction(actionEnvironment);

		// === act ===
		// NOTE: parsed instants below use Z to denote UTC timezone, which we define above, but that's not always a given

		// 2026-11-21 is a Saturday, so even though this is in range, it should not become done
		time[0] = Instant.parse("2026-11-21T23:30:00.000Z");
		action.update();
		assertFalse(action.isDone());

		// 2026-11-22 is a Sunday, but this is the second half of the overlapping period, so it should not be done for Sunday morning
		time[0] = Instant.parse("2026-11-22T02:30:00.000Z");
		action.update();
		assertFalse(action.isDone());

		// Sunday night right before the period starts
		time[0] = Instant.parse("2026-11-22T22:59:00.000Z");
		action.update();
		assertFalse(action.isDone());

		// Sunday night right when the period starts
		time[0] = Instant.parse("2026-11-22T23:00:00.000Z");
		action.update();
		assertTrue(action.isDone());

		// Sunday night during the period right before the next day
		time[0] = Instant.parse("2026-11-22T23:59:00.000Z");
		action.update();
		assertTrue(action.isDone());

		// Monday morning, still during the period
		time[0] = Instant.parse("2026-11-22T23:59:00.000Z");
		action.update();
		assertTrue(action.isDone());

		// Monday morning, period has ended
		time[0] = Instant.parse("2026-11-23T03:30:00.000Z");
		action.update();
		assertFalse(action.isDone());
	}
}
