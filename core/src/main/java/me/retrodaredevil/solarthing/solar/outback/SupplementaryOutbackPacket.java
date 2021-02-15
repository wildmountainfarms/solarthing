package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;

public interface SupplementaryOutbackPacket extends SupplementaryIdentifiable, OutbackData, Packet {
	@Override
	@NotNull KnownSupplementaryIdentifier<OutbackIdentifier> getIdentifier();
}
