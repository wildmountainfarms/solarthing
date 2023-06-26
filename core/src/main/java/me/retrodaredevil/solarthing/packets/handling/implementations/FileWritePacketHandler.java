package me.retrodaredevil.solarthing.packets.handling.implementations;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileWritePacketHandler implements PacketHandler {
	private final Path file;
	private final StringPacketHandler stringPacketHandler;
	private final boolean append;

	public FileWritePacketHandler(Path file, StringPacketHandler stringPacketHandler, boolean append) {
		this.file = file;
		this.stringPacketHandler = stringPacketHandler;
		this.append = append;
	}

	@Override
	public void handle(PacketCollection packetCollection) throws PacketHandleException {
		String string = stringPacketHandler.getString(packetCollection);
		try {
			Files.write(file, string.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE, (append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING));
		} catch (IOException e) {
			throw new PacketHandleException(e);
		}
	}
}
