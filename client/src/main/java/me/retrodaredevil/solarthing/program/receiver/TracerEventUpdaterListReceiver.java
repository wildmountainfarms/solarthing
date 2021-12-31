package me.retrodaredevil.solarthing.program.receiver;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPacket;
import me.retrodaredevil.solarthing.solar.tracer.event.ImmutableTracerChargingEquipmentStatusChangePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TracerEventUpdaterListReceiver implements PacketListReceiver {
	private final PacketListReceiver eventReceiver;

	private final Map<Identifier, TracerStatusPacket> previousPacketMap = new HashMap<>();

	public TracerEventUpdaterListReceiver(PacketListReceiver eventReceiver) {
		this.eventReceiver = eventReceiver;
	}

	@Override
	public void receive(List<Packet> packets) {
		for (Packet packet : packets) {
			if (packet instanceof TracerStatusPacket) {
				TracerStatusPacket tracer = (TracerStatusPacket) packet;
				TracerStatusPacket previous = previousPacketMap.get(tracer.getIdentifier());
				previousPacketMap.put(tracer.getIdentifier(), tracer);
				useData(tracer, previous);
			}
		}
	}
	private void useData(@NotNull TracerStatusPacket tracer, @Nullable TracerStatusPacket previous) {
		final Integer lastChargingEquipmentStatusValue;
		if (previous == null) {
			lastChargingEquipmentStatusValue = null;
		} else {
			lastChargingEquipmentStatusValue = previous.getChargingEquipmentStatus();
		}
		int currentChargingEquipmentStatusValue = tracer.getChargingEquipmentStatus();

		List<Packet> packets = new ArrayList<>();
		if (lastChargingEquipmentStatusValue == null || lastChargingEquipmentStatusValue != currentChargingEquipmentStatusValue) {
			packets.add(new ImmutableTracerChargingEquipmentStatusChangePacket(tracer.getIdentifier(), currentChargingEquipmentStatusValue, lastChargingEquipmentStatusValue));
		}

		if (!packets.isEmpty()) {
			eventReceiver.receive(packets);
		}
	}
}
