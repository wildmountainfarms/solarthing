package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.commands.CommandInfo;

import static java.util.Objects.requireNonNull;

public final class AvailableCommand {
	private final int fragmentId;
	private final CommandInfo commandInfo;

	public AvailableCommand(int fragmentId, CommandInfo commandInfo) {
		this.fragmentId = fragmentId;
		requireNonNull(this.commandInfo = commandInfo);
	}

	public int getFragmentId() {
		return fragmentId;
	}

	public CommandInfo getCommandInfo() {
		return commandInfo;
	}

	public String getPermission() {
		return "solarthing.command." + fragmentId + "." + commandInfo.getName();
	}
}
