package me.retrodaredevil.solarthing.solar.pzem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiable;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.common.Shunt;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@JsonDeserialize(as = ImmutablePzemShuntStatusPacket.class)
@JsonTypeName("PZEM_SHUNT")
@NullMarked
public interface PzemShuntStatusPacket extends SolarStatusPacket, PzemShuntReadTable, Shunt, DataIdentifiable {
	// TODO remove NonNull
	@DefaultFinal
	@Override
	default @NonNull SolarStatusPacketType getPacketType() {
		return SolarStatusPacketType.PZEM_SHUNT;
	}

	@JsonProperty("modbusAddress")
	int getModbusAddress();
}
