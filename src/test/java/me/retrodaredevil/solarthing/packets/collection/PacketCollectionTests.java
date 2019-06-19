package me.retrodaredevil.solarthing.packets.collection;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class PacketCollectionTests {
	@Test
	void testHourInterval(){
		PacketCollectionIdGenerator idGenerator = new HourIntervalPacketCollectionIdGenerator(60, null);
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, 2019);
		cal.set(Calendar.MONTH, 0); // january
		cal.set(Calendar.DAY_OF_MONTH, 1); // january 1
		cal.set(Calendar.HOUR_OF_DAY, 12); // noon
		
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals("2019,1,1,12,(1/60)", idGenerator.generateId(cal));
		
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 999);
		assertEquals("2019,1,1,12,(1/60)", idGenerator.generateId(cal));
		
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals("2019,1,1,12,(2/60)", idGenerator.generateId(cal));
		
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals("2019,1,1,12,(31/60)", idGenerator.generateId(cal));
		
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals("2019,1,1,12,(60/60)", idGenerator.generateId(cal));
		
		cal.set(Calendar.HOUR_OF_DAY, 13);
		cal.set(Calendar.MINUTE, 45);
		cal.set(Calendar.MILLISECOND, 250);
		assertEquals("2019,1,1,13,(46/60)", idGenerator.generateId(cal));
	}
	@Test
	void testHourIntervalWithUniqueCode() {
		PacketCollectionIdGenerator idGenerator = new HourIntervalPacketCollectionIdGenerator(60, 255);
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, 2019);
		cal.set(Calendar.MONTH, 0); // january
		cal.set(Calendar.DAY_OF_MONTH, 1); // january 1
		cal.set(Calendar.HOUR_OF_DAY, 12); // noon
		
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals("2019,1,1,12,(1/60),[ff]", idGenerator.generateId(cal));
	}
}
