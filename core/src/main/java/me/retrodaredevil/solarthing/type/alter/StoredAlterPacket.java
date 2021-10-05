package me.retrodaredevil.solarthing.type.alter;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.PacketEntry;

public interface StoredAlterPacket extends PacketEntry {
	@JsonProperty("updatedDateMillis")
	long getUpdatedDateMillis();
}
