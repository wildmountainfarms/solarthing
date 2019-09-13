package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.commands.OnCommandExecute;
import me.retrodaredevil.solarthing.commands.SourcedCommand;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.solar.outback.command.packets.ImmutableSuccessMateCommandPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class OnMateCommandSent implements OnCommandExecute<MateCommand> {
	
	private static final Logger LOGGER = LogManager.getLogger(OnMateCommandSent.class);
	
	private final PacketHandler packetHandler;
	
	public OnMateCommandSent(PacketHandler packetHandler){
		this.packetHandler = packetHandler;
	}
	@Override
	public void onCommandExecute(SourcedCommand<MateCommand> command) {
		Packet packet = new ImmutableSuccessMateCommandPacket(command.getCommand(), command.getSource());
		try {
			packetHandler.handle(PacketCollections.createFromPackets(Collections.singleton(packet), PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR), true);
		} catch (PacketHandleException e) {
			LOGGER.error("Couldn't save feedback packet for command: " + command.getCommand() + " from source: " + command.getSource(), e);
		}
	}
}
