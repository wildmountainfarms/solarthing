package me.retrodaredevil.solarthing.program.receiver;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.event.ImmutableRoverChargingStateChangePacket;

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
	public void receive(List<Packet> packets, InstantType instantType) {
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
		if (previous == null) {
			lastChargingStateValue = null;
		} else {
			lastChargingStateValue = previous.getChargingStateValue();
		}
		int currentChargingStateValue = rover.getChargingStateValue();

		List<Packet> packets = new ArrayList<>();
		if (lastChargingStateValue == null || lastChargingStateValue != currentChargingStateValue) {
			packets.add(new ImmutableRoverChargingStateChangePacket(rover.getIdentifier(), currentChargingStateValue, lastChargingStateValue));
		}
		if (!packets.isEmpty()) {
			eventReceiver.receive(packets, InstantType.INSTANT); // TODO remove instant type // doesn't apply to rovers
		}
	}
}
