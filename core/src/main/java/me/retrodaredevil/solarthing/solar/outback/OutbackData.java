package me.retrodaredevil.solarthing.solar.outback;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.TagKeys;

@TagKeys({"address"})
public interface OutbackData {
	/**
	 * Should be serialized as "address"
	 * @return [0..10] The address of the port that the device that sent this packet is plugged in to. If 0, this device is plugged directly into the Mate
	 */
	@JsonProperty("address")
	int getAddress();
}
