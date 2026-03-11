package me.retrodaredevil.solarthing.solar.outback;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import org.jspecify.annotations.NonNull;


@JsonPropertyOrder({"packetType", "address"})
public interface OutbackStatusPacket extends SolarStatusPacket, ErrorReporter, OutbackData {
	@Override
	@NonNull OutbackIdentifier getIdentifier();
}
