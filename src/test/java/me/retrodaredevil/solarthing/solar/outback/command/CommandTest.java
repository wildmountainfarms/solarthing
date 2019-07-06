package me.retrodaredevil.solarthing.solar.outback.command;

import me.retrodaredevil.solarthing.solar.outback.command.sequence.CommandSequence;
import me.retrodaredevil.solarthing.solar.outback.command.sequence.CommandSequenceCommandProvider;
import me.retrodaredevil.solarthing.solar.outback.command.sequence.condition.Condition;
import me.retrodaredevil.solarthing.solar.outback.command.sequence.condition.ConditionTask;
import me.retrodaredevil.solarthing.solar.outback.command.sequence.condition.TimedCondition;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

final class CommandTest {
	@Test
	void testTimedCondition(){
		long[] time = new long[]{0};
		Condition condition = new TimedCondition(500, () -> time[0]);
		ConditionTask task = condition.start();
		assertFalse(task.isDone());
		time[0] = 499;
		assertFalse(task.isDone());
		time[0] = 500;
		assertTrue(task.isDone());
	}
	@Test
	void testCommandSequence(){
		long[] time = new long[]{0};
		CommandSequence commandSequence = new CommandSequence.Builder()
			.append(new TimedCondition(500, () -> time[0]), MateCommand.AUX_ON)
			.append(new TimedCondition(1000, () -> time[0]), MateCommand.AUX_OFF)
			.build();
		CommandProvider commandProvider = new CommandSequenceCommandProvider(new LinkedList<>(Collections.singletonList(commandSequence))::poll);
		assertNull(commandProvider.pollCommand());
		time[0] = 499;
		assertNull(commandProvider.pollCommand());
		time[0] = 500;
		assertEquals(MateCommand.AUX_ON, commandProvider.pollCommand());
		assertNull(commandProvider.pollCommand());
		time[0] = 1499;
		assertNull(commandProvider.pollCommand());
		time[0] = 1500;
		assertEquals(MateCommand.AUX_OFF, commandProvider.pollCommand());
		assertNull(commandProvider.pollCommand());
	}
}
