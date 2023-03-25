package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.config.request.modbus.ModbusDataRequester;

/**
 * A {@link DataRequester} returns a {@link DataRequesterResult}, which can allow the addition of
 * packets to the packet collection that will end up in the status database.
 * The resulting {@link DataRequesterResult} can also update the action environment for actions that
 * are executed. The intent for updating the action environment is for interacting with the device
 * through commands. Although these can also be used for other purposes
 */
@JsonSubTypes({
		@JsonSubTypes.Type(RaspberryPiCpuTemperatureDataRequester.class),
		@JsonSubTypes.Type(CpuTemperatureDataRequester.class),
		@JsonSubTypes.Type(W1TemperatureDataRequester.class),
		@JsonSubTypes.Type(BatteryVoltageIODataRequester.class),
		@JsonSubTypes.Type(PzemShuntDataRequester.class),
		@JsonSubTypes.Type(ModbusDataRequester.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface DataRequester {

	DataRequesterResult create(RequestObject requestObject);

}
