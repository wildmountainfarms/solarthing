package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.type.alter.UniqueRequestIdContainer;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.WorkInProgress;

@WorkInProgress
@JsonDeserialize // need this here for ArchTest to pass, add "as = ..." once we have an implementation class
@JsonTypeName("SCHEDULE_COMMAND")
public interface ScheduleCommandPacket extends CommandOpenPacket, UniqueRequestIdContainer {
	@DefaultFinal
	@Override
	default @NotNull CommandOpenPacketType getPacketType() {
		return CommandOpenPacketType.SCHEDULE_COMMAND;
	}

	@JsonProperty("scheduledTimeMillis")
	long getScheduledTimeMillis();

	// TODO we need more information than just commandName (targets)
	@JsonProperty("commandName")
	@NotNull String getCommandName();
}
