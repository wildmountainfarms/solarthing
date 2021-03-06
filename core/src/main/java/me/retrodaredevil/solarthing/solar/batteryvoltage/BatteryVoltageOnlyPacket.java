package me.retrodaredevil.solarthing.solar.batteryvoltage;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiable;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;

@JsonDeserialize(as = ImmutableBatteryVoltageOnlyPacket.class)
@JsonTypeName("BATTERY_VOLTAGE_ONLY")
@JsonExplicit
public interface BatteryVoltageOnlyPacket extends BatteryVoltage, DataIdentifiable, SolarStatusPacket {
	@DefaultFinal
	@Override
	default @NotNull SolarStatusPacketType getPacketType() {
		return SolarStatusPacketType.BATTERY_VOLTAGE_ONLY;
	}
}
