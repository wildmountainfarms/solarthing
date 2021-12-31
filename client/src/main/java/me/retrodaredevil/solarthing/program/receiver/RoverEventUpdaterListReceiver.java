package me.retrodaredevil.solarthing.program.receiver;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.event.ImmutableRoverChargingStateChangePacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.event.ImmutableRoverErrorModeChangePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoverEventUpdaterListReceiver implements PacketListReceiver {

	private final PacketListReceiver eventReceiver;

	private final Map<Identifier, RoverStatusPacket> previousPacketMap = new HashMap<>();

	public RoverEventUpdaterListReceiver(PacketListReceiver eventReceiver) {
		this.eventReceiver = eventReceiver;
	}

	@Override
	public void receive(List<Packet> packets) {
		for (Packet packet : packets) {
			if (packet instanceof RoverStatusPacket) {
				RoverStatusPacket rover = (RoverStatusPacket) packet;
				RoverStatusPacket previous = previousPacketMap.get(rover.getIdentifier());
				previousPacketMap.put(rover.getIdentifier(), rover);
				useData(rover, previous);
			}
		}
	}
	private void useData(@NotNull RoverStatusPacket rover, @Nullable RoverStatusPacket previous) {
		final Integer lastChargingStateValue;
		final Integer lastErrorModeValue;
		if (previous == null) {
			lastChargingStateValue = null;
			lastErrorModeValue = null;
		} else {
			lastChargingStateValue = previous.getChargingStateValue();
			lastErrorModeValue = previous.getErrorModeValue();
		}
		int currentChargingStateValue = rover.getChargingStateValue();
		int currentErrorModeValue = rover.getErrorModeValue();

		List<Packet> packets = new ArrayList<>();
		if (lastChargingStateValue == null || lastChargingStateValue != currentChargingStateValue) {
			packets.add(new ImmutableRoverChargingStateChangePacket(rover.getIdentifier(), currentChargingStateValue, lastChargingStateValue));
		}
		if (lastErrorModeValue == null || lastErrorModeValue != currentErrorModeValue) {
			packets.add(new ImmutableRoverErrorModeChangePacket(rover.getIdentifier(), currentErrorModeValue, lastErrorModeValue));
		}
		if (!packets.isEmpty()) {
			eventReceiver.receive(packets);
		}
	}
}
