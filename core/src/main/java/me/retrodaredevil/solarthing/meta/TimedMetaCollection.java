package me.retrodaredevil.solarthing.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.util.TimeRange;

import java.util.List;

public class TimedMetaCollection {
	private final TimeRange timeRange;
	private final List<MetaPacket> packets;

	public TimedMetaCollection(
			@JsonProperty("start") Long startTime,
			@JsonProperty("end") Long endTime,
			@JsonProperty(value = "packets", required = true) List<MetaPacket> packets
	) {
		timeRange = new TimeRange(startTime, endTime);
		this.packets = packets;
	}
}
