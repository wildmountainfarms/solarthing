package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.util.Collection;

@JsonTypeName("TARGET")
@JsonDeserialize(as = ImmutableInstanceTargetPacket.class)
@JsonExplicit
public interface InstanceTargetPacket extends InstancePacket, TargetPacket {

	@Override
	default @NotNull InstancePacketType getPacketType() {
		return InstancePacketType.TARGET;
	}

	@JsonProperty("targets")
	@Override
	@Nullable Collection<Integer> getTargetFragmentIds();
}
