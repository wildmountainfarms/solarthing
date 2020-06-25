package me.retrodaredevil.solarthing.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.List;

@JsonTypeName("FRAGMENT_TARGET")
@JsonExplicit
public class TargetMetaPacket implements BasicMetaPacket {
	private final List<Integer> fragmentIds;
	private final List<TargetedMetaPacket> packets;

	@JsonCreator
	public TargetMetaPacket(
			@JsonProperty(value = "fragmentIds", required = true) List<Integer> fragmentIds,
			@JsonProperty(value = "packets", required = true) List<TargetedMetaPacket> packets
	) {
		this.fragmentIds = fragmentIds;
		this.packets = packets;
	}

	@Override
	@NotNull
	public BasicMetaPacketType getPacketType() {
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
