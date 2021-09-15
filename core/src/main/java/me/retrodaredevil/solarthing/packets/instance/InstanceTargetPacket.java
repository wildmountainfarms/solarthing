package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.marker.OpenPacket;

import java.util.Collection;

/**
 * A packet that is stored alongside other packets in a packet collection, that are stored in the {@link me.retrodaredevil.solarthing.SolarThingConstants#OPEN_DATABASE}.
 */
@JsonTypeName("TARGET")
@JsonDeserialize(as = ImmutableInstanceTargetPacket.class)
@JsonExplicit
public interface InstanceTargetPacket extends InstancePacket, TargetPacket, OpenPacket {

	@DefaultFinal
	@Override
	default @NotNull InstancePacketType getPacketType() {
		return InstancePacketType.TARGET;
	}

	@JsonProperty("targets")
	@Override
	@Nullable Collection<Integer> getTargetFragmentIds();
}
