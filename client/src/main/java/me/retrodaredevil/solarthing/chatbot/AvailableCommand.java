package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.commands.CommandInfo;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class AvailableCommand {
	private final int fragmentId;
	private final CommandInfo commandInfo;

	public AvailableCommand(int fragmentId, CommandInfo commandInfo) {
		this.fragmentId = fragmentId;
		this.commandInfo = requireNonNull(commandInfo);
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
