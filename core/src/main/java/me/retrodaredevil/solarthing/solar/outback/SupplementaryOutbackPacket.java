package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SupplementaryOutbackPacket extends SupplementaryIdentifiable, OutbackData, Packet {
	@Override
	KnownSupplementaryIdentifier<OutbackIdentifier> getIdentifier();
}
