package me.retrodaredevil.solarthing.pvoutput;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class SimpleDateTest {
	@Test
	void testDate(){
		assertEquals("20191217", new SimpleDate(2019, 12, 17).toPVOutputString());
	}
}
