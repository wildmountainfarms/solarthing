package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.Numbered;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;

public interface SupplementaryRoverPacket extends SupplementaryIdentifiable, Packet, Numbered {

	// Unlike the status packet, "number" will always be included
	@JsonProperty("number")
	@Override
	int getNumber();

	@Override
	@NotNull KnownSupplementaryIdentifier<RoverIdentifier> getIdentifier();
}
