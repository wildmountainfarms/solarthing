package me.retrodaredevil.solarthing.packets.handling.implementations;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FileWritePacketHandler implements PacketHandler {
	private final File file;
	private final StringPacketHandler stringPacketHandler;
	private final boolean append;
	
	public FileWritePacketHandler(File file, StringPacketHandler stringPacketHandler, boolean append) {
		this.file = file;
		this.stringPacketHandler = stringPacketHandler;
		this.append = append;
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		String string = stringPacketHandler.getString(packetCollection);
		try {
			Files.writeString(file.toPath(), string, StandardOpenOption.CREATE, (append ? StandardOpenOption.APPEND : StandardOpenOption.WRITE));
		} catch (IOException e) {
			throw new PacketHandleException(e);
		}
	}
}
