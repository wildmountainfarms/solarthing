package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.outback.mx.event.ImmutableMXAuxModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.ImmutableMXChargerModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.ImmutableMXErrorModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.ImmutableMXRawDayEndPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MXEventUpdaterListReceiver implements PacketListReceiver {
	private final PacketListReceiver eventReceiver;
	private final Map<Identifier, MXStatusPacket> previousPacketMap = new HashMap<>();

	public MXEventUpdaterListReceiver(PacketListReceiver eventReceiver) {
		this.eventReceiver = eventReceiver;
	}

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		for(Packet packet : packets){
			if(packet instanceof MXStatusPacket){
				MXStatusPacket mx = (MXStatusPacket) packet;
				MXStatusPacket previous = previousPacketMap.get(mx.getIdentifier());
				previousPacketMap.put(mx.getIdentifier(), mx);
				useData(mx, previous, instantType);
			}
		}
	}
	private void useData(MXStatusPacket mx, MXStatusPacket last, InstantType instantType){
		final Integer previousChargerModeValue;
		final Integer previousRawAuxModeValue;
		final Integer previousErrorModeValue;
		if (last == null) {
			previousChargerModeValue = null;
			previousRawAuxModeValue = null;
			previousErrorModeValue = null;
		} else {
			previousChargerModeValue = last.getChargerModeValue();
			previousRawAuxModeValue = last.getRawAuxModeValue();
			previousErrorModeValue = last.getErrorModeValue();
		}
		int chargerModeValue = mx.getChargerModeValue();
		int rawAuxModeValue = mx.getRawAuxModeValue();
		int errorModeValue = mx.getErrorModeValue();
		List<Packet> eventPackets = new ArrayList<>();
		if (previousChargerModeValue == null || chargerModeValue != previousChargerModeValue) {
			eventPackets.add(new ImmutableMXChargerModeChangePacket(mx.getIdentifier(), chargerModeValue, previousChargerModeValue));
		}
		if (previousRawAuxModeValue == null || rawAuxModeValue != previousRawAuxModeValue) {
			eventPackets.add(new ImmutableMXAuxModeChangePacket(mx.getIdentifier(), rawAuxModeValue, previousRawAuxModeValue));
		}
		if(previousErrorModeValue == null || errorModeValue != previousErrorModeValue){
			eventPackets.add(new ImmutableMXErrorModeChangePacket(mx.getIdentifier(), errorModeValue, previousErrorModeValue));
		}
		if (last != null && mx.isNewDay(last)) {
			eventPackets.add(new ImmutableMXRawDayEndPacket(mx.getAddress(), mx.getDailyKWH(), mx.getDailyAH(), mx.getDailyAHSupport()));
		}
		if (!eventPackets.isEmpty()) {
			eventReceiver.receive(eventPackets, instantType);
		}
	}
}
