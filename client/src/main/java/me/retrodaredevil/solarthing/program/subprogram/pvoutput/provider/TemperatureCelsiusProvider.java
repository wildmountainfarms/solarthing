package me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({
		@JsonSubTypes.Type(PacketTemperatureCelsiusProvider.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface TemperatureCelsiusProvider extends DataProvider {
	TemperatureCelsiusProvider NONE = packet -> null;
}
