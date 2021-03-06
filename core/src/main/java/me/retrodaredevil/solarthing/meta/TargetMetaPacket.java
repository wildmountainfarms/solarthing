package me.retrodaredevil.solarthing.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("FRAGMENT_TARGET")
@JsonExplicit
public final class TargetMetaPacket implements BasicMetaPacket {
	private final List<Integer> fragmentIds;
	private final List<TargetedMetaPacket> packets;

	@JsonCreator
	public TargetMetaPacket(
			@JsonProperty(value = "fragmentIds", required = true) List<Integer> fragmentIds,
			@JsonProperty(value = "packets", required = true) List<TargetedMetaPacket> packets
	) {
		this.fragmentIds = fragmentIds;
		this.packets = new ArrayList<>();
		for (TargetedMetaPacket packet : packets) {
			if (packet != null) {
				this.packets.add(packet);
			}
		}
	}

	@Override
	public @NotNull BasicMetaPacketType getPacketType() {
		return BasicMetaPacketType.FRAGMENT_TARGET;
	}

	@JsonProperty("fragmentIds")
	public List<Integer> getFragmentIds() {
		return fragmentIds;
	}

	@JsonProperty("packets")
	public List<TargetedMetaPacket> getPackets() {
		return packets;
	}
}
