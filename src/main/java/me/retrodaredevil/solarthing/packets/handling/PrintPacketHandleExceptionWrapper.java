package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

import java.io.PrintStream;

public class PrintPacketHandleExceptionWrapper implements PacketHandler {
	private final PacketHandler packetHandler;
	private final PrintStream printStream;
	
	public PrintPacketHandleExceptionWrapper(PacketHandler packetHandler, PrintStream printStream) {
		this.packetHandler = packetHandler;
		this.printStream = printStream;
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) {
		try {
			packetHandler.handle(packetCollection, wasInstant);
		} catch (PacketHandleException e) {
			e.printUnableToHandle(printStream, "Caught PacketHandleException from " + packetHandler);
		}
	}
}
