package me.retrodaredevil.solarthing.solar.pzem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiable;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.common.Shunt;

@JsonDeserialize(as = ImmutablePzemShuntStatusPacket.class)
@JsonTypeName("PZEM_SHUNT")
public interface PzemShuntStatusPacket extends SolarStatusPacket, PzemShuntReadTable, Shunt, DataIdentifiable {
	@DefaultFinal
	@Override
	default @NotNull SolarStatusPacketType getPacketType() {
		return SolarStatusPacketType.PZEM_SHUNT;
	}

	@JsonProperty("modbusAddress")
	int getModbusAddress();
}
