package me.retrodaredevil.solarthing.solar.outback;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;


@JsonPropertyOrder({"packetType", "address"})
@NullMarked
public interface OutbackStatusPacket extends SolarStatusPacket, ErrorReporter, OutbackData {
	// TODO remove NonNull
	@Override
	@NonNull OutbackIdentifier getIdentifier();
}
