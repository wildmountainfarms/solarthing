package me.retrodaredevil.solarthing.solar;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;

public interface SolarPacket extends DocumentedPacket<SolarPacketType>, Identifiable {
	@Override
	Identifier getIdentifier();
}
