package me.retrodaredevil.solarthing.solar.outback;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;


@JsonPropertyOrder({"packetType", "address"})
public interface OutbackStatusPacket extends SolarStatusPacket, ErrorReporter, OutbackData {
	@Override
	@NotNull OutbackIdentifier getIdentifier();
}
