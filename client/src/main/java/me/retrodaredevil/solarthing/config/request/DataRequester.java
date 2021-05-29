package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({
		@JsonSubTypes.Type(RaspberryPiCpuTemperatureDataRequester.class),
		@JsonSubTypes.Type(W1TemperatureDataRequester.class),
		@JsonSubTypes.Type(BatteryVoltageIODataRequester.class),
		@JsonSubTypes.Type(PzemShuntDataRequester.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface DataRequester {

	DataRequesterResult create(RequestObject requestObject);

}
