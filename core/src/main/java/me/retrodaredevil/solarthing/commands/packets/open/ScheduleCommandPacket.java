package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.alter.UniqueRequestIdContainer;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.NotNull;

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
