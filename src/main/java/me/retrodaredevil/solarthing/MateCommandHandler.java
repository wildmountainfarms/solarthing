package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.solar.outback.MateCommand;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;

import static java.util.Objects.requireNonNull;

public class MateCommandHandler implements PacketHandler {
	private final Queue<MateCommand> queue;
	private final OutputStream outputStream;
	
	public MateCommandHandler(Queue<MateCommand> queue, OutputStream outputStream) {
		this.queue = requireNonNull(queue);
		this.outputStream = requireNonNull(outputStream);
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		if(wasInstant){
			MateCommand command = queue.poll();
			if(command != null){
				try {
					command.send(outputStream);
				} catch (IOException e) {
					throw new PacketHandleException("Unable to send command: " + command, e);
				}
			}
		}
	}
}
