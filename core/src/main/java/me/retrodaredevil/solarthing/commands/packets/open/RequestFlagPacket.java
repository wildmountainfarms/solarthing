package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.type.alter.flag.FlagData;

/**
 * Represents the request to schedule when a certain flag is active. This is usually handled by the automation program
 * and a corresponding packet is put in {@link me.retrodaredevil.solarthing.SolarThingConstants#ALTER_DATABASE} assuming the sender
 * is authorized to perform this request
 */
@JsonDeserialize(as = ImmutableRequestFlagPacket.class)
@JsonTypeName("REQUEST_FLAG")
@JsonExplicit
public interface RequestFlagPacket extends CommandOpenPacket {

	@DefaultFinal
	@Override
	default @NotNull CommandOpenPacketType getPacketType() {
		return CommandOpenPacketType.REQUEST_FLAG;
	}

	@JsonProperty("flagData")
	@NotNull FlagData getFlagData();
}
