package me.retrodaredevil.solarthing.packets.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import me.retrodaredevil.solarthing.annotations.NotNull;

/**
 * This packet is used to indicate that multiple {@link me.retrodaredevil.solarthing.packets.collection.PacketCollection}s
 * in a database are fragmented. This means that packet collection A (fragment id of 1) and packet collection B (fragment id of 2)
 * should be treated as one packet IF a particular B packet collection is the closest B packet collection to packet collection A.
 * <p>
 * NOTE: This packet should be supplemented by a {@link InstanceSourcePacket} in the same packet collection
 */
@JsonTypeName("FRAGMENT_INDICATOR")
@JsonDeserialize(as = ImmutableInstanceFragmentIndicatorPacket.class)
@JsonExplicit
public interface InstanceFragmentIndicatorPacket extends InstancePacket {

	@DefaultFinal
	@Override
	default @NotNull InstancePacketType getPacketType(){
		return InstancePacketType.FRAGMENT_INDICATOR;
	}

	/**
	 * Should be serialized as "fragmentId"
	 * @return The fragment id. The lowest fragment id is the "master" fragment packet collection
	 */
	@JsonProperty("fragmentId")
	int getFragmentId();
}
