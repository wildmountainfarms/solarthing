package me.retrodaredevil.solarthing.packets.collection;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class PacketCollectionTests {
	@Test
	void testHourInterval(){
		PacketCollectionIdGenerator idGenerator = new HourIntervalPacketCollectionIdGenerator(60, null);
		assertEquals("2019,01,01,12,(01/60)", idGenerator.generateId(
				LocalDateTime.of(
						LocalDate.of(2019, 1, 1),
						LocalTime.of(12, 0, 0)
				).atZone(ZoneId.systemDefault())
		));

		assertEquals("2019,01,01,12,(01/60)", idGenerator.generateId(
				LocalDateTime.of(
						LocalDate.of(2019, 1, 1),
						LocalTime.of(12, 0, 0, 999_999_999)
				).atZone(ZoneId.systemDefault())
		));

		assertEquals("2019,01,01,12,(02/60)", idGenerator.generateId(
				LocalDateTime.of(
						LocalDate.of(2019, 1, 1),
						LocalTime.of(12, 1, 0)
				).atZone(ZoneId.systemDefault())
		));

		assertEquals("2019,01,01,12,(31/60)", idGenerator.generateId(
				LocalDateTime.of(
						LocalDate.of(2019, 1, 1),
						LocalTime.of(12, 30, 0)
				).atZone(ZoneId.systemDefault())
		));

		assertEquals("2019,01,01,12,(60/60)", idGenerator.generateId(
				LocalDateTime.of(
						LocalDate.of(2019, 1, 1),
						LocalTime.of(12, 59, 1)
				).atZone(ZoneId.systemDefault())
		));

		assertEquals("2019,01,01,13,(46/60)", idGenerator.generateId(
				LocalDateTime.of(
						LocalDate.of(2019, 1, 1),
						LocalTime.of(13, 45, 0, 250_000_000)
				).atZone(ZoneId.systemDefault())
		));
	}
	@Test
	void testHourIntervalWithUniqueCode() {
		PacketCollectionIdGenerator idGenerator = new HourIntervalPacketCollectionIdGenerator(60, 255);
		ZonedDateTime zonedDateTime = LocalDateTime.of(
				LocalDate.of(2019, 1, 1),
				LocalTime.of(12, 0, 0)
		).atZone(ZoneId.systemDefault());
		assertEquals("2019,01,01,12,(01/60),[000000ff]", idGenerator.generateId(zonedDateTime));
	}
	@Test
	void testHourIntervalFewUnique() {
		PacketCollectionIdGenerator idGenerator = new HourIntervalPacketCollectionIdGenerator(9, null);
		ZonedDateTime zonedDateTime = LocalDateTime.of(
				LocalDate.of(2019, 1, 1),
				LocalTime.of(9, 0, 0)
		).atZone(ZoneId.systemDefault());
		assertEquals("2019,01,01,09,(1/9)", idGenerator.generateId(zonedDateTime));
	}
	@Test
	void testHourIntervalShort(){
		PacketCollectionIdGenerator idGenerator = new HourIntervalPacketCollectionIdGenerator(60, null, true);
		assertEquals("190101,12,01/60", idGenerator.generateId(
				LocalDateTime.of(
						LocalDate.of(2019, 1, 1),
						LocalTime.of(12, 0, 0)
				).atZone(ZoneId.systemDefault())
		));

		assertEquals("190101,12,01/60", idGenerator.generateId(
				LocalDateTime.of(
						LocalDate.of(2019, 1, 1),
						LocalTime.of(12, 0, 0, 999_999_999)
				).atZone(ZoneId.systemDefault())
		));

		assertEquals("190101,12,02/60", idGenerator.generateId(
				LocalDateTime.of(
						LocalDate.of(2019, 1, 1),
						LocalTime.of(12, 1, 0)
				).atZone(ZoneId.systemDefault())
		));
	}
	@Test
	void testHourIntervalWithShortUniqueCode() {
		PacketCollectionIdGenerator idGenerator = new HourIntervalPacketCollectionIdGenerator(60, 1173894740, true);
		ZonedDateTime zonedDateTime = LocalDateTime.of(
				LocalDate.of(2019, 1, 1),
				LocalTime.of(12, 0, 0)
		).atZone(ZoneId.systemDefault());
		assertEquals("190101,12,01/60|Rfg2VA", idGenerator.generateId(zonedDateTime));
	}
}
