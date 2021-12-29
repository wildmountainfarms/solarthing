package me.retrodaredevil.solarthing.solar.tracer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.VersionedPacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;

@JsonExplicit
@JsonTypeName("TRACER_STATUS")
@JsonDeserialize(as = ImmutableTracerStatusPacket.class)
public interface TracerStatusPacket extends TracerReadTable, SolarStatusPacket, VersionedPacket {

	int CHARGING_EQUIPMENT_FIX_VERSION = 1;

	@Override
	default @NotNull SolarStatusPacketType getPacketType() {
		return SolarStatusPacketType.TRACER_STATUS;
	}

	@Override
	@NotNull TracerIdentifier getIdentifier();

	// TODO implement NumberedIdentifiable (ImmutableTracerStatusPacket already handles this fine)
}
