package me.retrodaredevil.solarthing.solar.outback;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;

public interface OutbackPacket extends SolarStatusPacket, ErrorReporter {
	/**
	 * Should be serialized as "address"
	 * @return [0..10] The address of the port that the device that sent this packet is plugged in to. If 0, this device is plugged directly into the Mate
	 */
	@JsonProperty(value = "address")
	int getAddress();
	
	@Override
	OutbackIdentifier getIdentifier();
}
