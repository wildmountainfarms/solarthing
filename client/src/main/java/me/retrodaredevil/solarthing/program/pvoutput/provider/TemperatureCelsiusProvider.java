package me.retrodaredevil.solarthing.program.pvoutput.provider;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({
		@JsonSubTypes.Type(TemperaturePacketTemperatureCelsiusProvider.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface TemperatureCelsiusProvider extends DataProvider {
}
