package me.retrodaredevil.solarthing.util.frequency;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

final class FrequentHandlerTest {
	@Test
	void testFrequentHandler(){
		FrequentObject<TestClass> o1 = new FrequentObject<>(new TestClass(), 4);
		FrequentObject<TestClass> o2 = new FrequentObject<>(new TestClass(), null);
		FrequentHandler<TestClass> handler = new FrequentHandler<>(Arrays.asList(o1, o2));

		for(int i = 0; i < 3; i++) {
			assertEquals(o1, handler.get(0));
			handler.use(o1);
			assertEquals(o2, handler.get(0));
			handler.use(o2);

			assertEquals(o2, handler.get(.24));
			handler.use(o2);
			assertEquals(o1, handler.get(.25));
			handler.use(o1);

			assertEquals(o1, handler.get(.5));
			handler.use(o1);

			assertEquals(o1, handler.get(.75));
			handler.use(o1);

			assertEquals(o2, handler.get(.9));
			handler.use(o2);

			handler.reset();
		}
	}

	private static final class TestClass {

	}

}
