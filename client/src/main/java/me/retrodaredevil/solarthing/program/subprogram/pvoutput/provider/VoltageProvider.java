package me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jspecify.annotations.NullMarked;

@JsonSubTypes({
		@JsonSubTypes.Type(PacketVoltageProvider.class),
		@JsonSubTypes.Type(AverageBatteryVoltageProvider.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@NullMarked
public interface VoltageProvider extends DataProvider {

	VoltageProvider NONE = packet -> null;
}
