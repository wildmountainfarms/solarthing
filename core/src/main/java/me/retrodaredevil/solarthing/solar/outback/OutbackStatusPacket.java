package me.retrodaredevil.solarthing.solar.outback;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;

import me.retrodaredevil.solarthing.annotations.NotNull;


@JsonPropertyOrder({"packetType", "address"})
public interface OutbackStatusPacket extends SolarStatusPacket, ErrorReporter, OutbackData {
	@GraphQLInclude("identifier")
	@Override
	@NotNull OutbackIdentifier getIdentifier();
}
