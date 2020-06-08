package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Calls {@link PacketHandler#handle(PacketCollection, InstantType)} for each {@link PacketHandler}. If a {@link PacketHandler}
 * throws a {@link PacketHandleException} or any exception for that matter, it will not call {@link PacketHandler#handle(PacketCollection, InstantType)}
 * on {@link PacketHandler}s it didn't get to, making order significant if you want to make sure {@link PacketHandler}s that cannot throw exceptions
 * are executed before ones that could throw an exception.
 */
public class PacketHandlerMultiplexer implements PacketHandler {
	private final List<PacketHandler> packetHandlers;

	public PacketHandlerMultiplexer(List<? extends PacketHandler> packetHandlers) {
		this.packetHandlers = Collections.unmodifiableList(new ArrayList<>(packetHandlers));
	}
	public PacketHandlerMultiplexer(PacketHandler... packetHandlers){
		this.packetHandlers = Arrays.asList(packetHandlers);
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) throws PacketHandleException {
		for(PacketHandler handler : packetHandlers){
			handler.handle(packetCollection, instantType);
		}
	}
}
