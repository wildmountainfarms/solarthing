package me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jspecify.annotations.NullMarked;

@JsonSubTypes({
		@JsonSubTypes.Type(PacketTemperatureCelsiusProvider.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@NullMarked
public interface TemperatureCelsiusProvider extends DataProvider {
	TemperatureCelsiusProvider NONE = packet -> null;
}
