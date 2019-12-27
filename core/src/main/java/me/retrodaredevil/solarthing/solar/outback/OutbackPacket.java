package me.retrodaredevil.solarthing.solar.outback;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;

@JsonPropertyOrder({"packetType", "address"})
public interface OutbackPacket extends OutbackData, SolarStatusPacket, ErrorReporter {

	@Override
	OutbackIdentifier getIdentifier();
}
