package me.retrodaredevil.solarthing.type.alter.flag;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import me.retrodaredevil.solarthing.util.UniqueStringRepresentation;

import java.time.Instant;

@JsonSubTypes({
		@JsonSubTypes.Type(TimeRangeActivePeriod.class),
})
public interface ActivePeriod extends TypedDocumentedPacket<ActivePeriodType>, UniqueStringRepresentation {

	boolean isActive(long dateMillis);
	boolean isActive(Instant instant);

	/**
	 * @param activePeriod The other active period to check to see if this fully encapsulates
	 * @return true if {@code this.}{@link #isActive(long)} returns true for every timestamp that {@code activePeriod.}{@link #isActive(long)} returns true
	 */
	boolean encapsulatesAllOf(ActivePeriod activePeriod);
}
