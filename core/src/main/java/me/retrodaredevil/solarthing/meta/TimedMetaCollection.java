package me.retrodaredevil.solarthing.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.util.TimeRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class TimedMetaCollection {
	// check this out https://github.com/FasterXML/jackson-databind/issues/511 we could use fail on invalid subtype


	private final TimeRange timeRange;
	private final List<BasicMetaPacket> packets;

	public TimedMetaCollection(Long startTime, Long endTime, List<BasicMetaPacket> packets) {
		timeRange = new TimeRange(startTime, endTime);
		requireNonNull(this.packets = packets);
	}
	@JsonCreator
	public static TimedMetaCollection createRemoveNullValues(
			@JsonProperty("start") Long startTime,
			@JsonProperty("end") Long endTime,
			@JsonProperty(value = "packets", required = true) List<BasicMetaPacket> nullablePackets
	) {
		return new TimedMetaCollection(
				startTime,
				endTime,
				nullablePackets.stream().filter(Objects::nonNull).collect(Collectors.toList())
		);
	}
	// TODO these JSON properties are not consistent.
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("startTime")
	public Long getStartTime() {
		return timeRange.getStartTimeMillis();
	}
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("endTime")
	public Long getEndTime() {
		return timeRange.getEndTimeMillis();
	}
	@JsonProperty("packets")
	public @NotNull List<BasicMetaPacket> getPackets() {
		return packets;
	}

	public TimeRange getTimeRange() {
		return timeRange;
	}
}
