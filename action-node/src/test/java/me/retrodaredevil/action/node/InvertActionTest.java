package me.retrodaredevil.action.node;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvertActionTest {

	@Test
	void test() {
		Action neverDone = Actions.createRunForever(() -> {});
		Action inverted = new InvertAction(neverDone);
		inverted.update();
		assertTrue(inverted.isDone());
	}

}
