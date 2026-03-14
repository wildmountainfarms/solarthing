package me.retrodaredevil.solarthing.util.time;

import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@NullMarked
public class DailyIdentifier implements TimeIdentifier {
	private final ZoneId zoneId;


	public DailyIdentifier(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	@Override
	public long getTimeId(long timeMillis) {
		LocalDateTime date = Instant.ofEpochMilli(timeMillis).atZone(zoneId).toLocalDateTime();
		return 400L * date.getYear() + date.getDayOfYear(); // this is arbitrary, so using 400 is fine
	}
}
