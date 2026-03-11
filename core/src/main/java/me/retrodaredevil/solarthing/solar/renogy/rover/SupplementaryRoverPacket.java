package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.Numbered;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;
import org.jspecify.annotations.NonNull;

public interface SupplementaryRoverPacket extends SupplementaryIdentifiable, Packet, Numbered {

	// Unlike the status packet, "number" will always be included
	@JsonProperty("number")
	@Override
	int getNumber();

	@Override
	@NonNull KnownSupplementaryIdentifier<RoverIdentifier> getIdentifier();
}
