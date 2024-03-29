package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.type.alter.UniqueRequestIdContainer;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandData;

import java.util.UUID;

/**
 * Represents the request to schedule a command for execution later. Schedule command packets are not handled directly by the targets they are destined for,
 * but instead are handled by the automation program, which will put state in {@link me.retrodaredevil.solarthing.SolarThingConstants#ALTER_DATABASE} assuming
 * the sender was authorized.
 * <p>
 * The resulting packet in the alter database is {@link me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandPacket}
 */
@JsonDeserialize(as = ImmutableScheduleCommandPacket.class)
@JsonTypeName("SCHEDULE_COMMAND")
@JsonExplicit
public interface ScheduleCommandPacket extends CommandOpenPacket, UniqueRequestIdContainer {
	@DefaultFinal
	@Override
	default @NotNull CommandOpenPacketType getPacketType() {
		return CommandOpenPacketType.SCHEDULE_COMMAND;
	}

	@JsonProperty("data")
	@NotNull ScheduledCommandData getData();

	/**
	 * Note: Although this UUID should be unique, the ID is generated by a client. Because of this, you should never rely on that
	 * this is unique if you are doing anything involved with security. However, if you are doing something cosmetic, then it is fine to
	 * <p>
	 * If you trust the client, then it is usually safe to assume this is unique
	 * @return The unique request ID. This should be randomly generated by a client requesting
	 */
	@Override
	@NotNull UUID getUniqueRequestId();

	// Like all packets in a PacketCollection, this packet will have a source ID associated with it which
	//   can be used when creating the packet collection with the request command packet in it.
}
