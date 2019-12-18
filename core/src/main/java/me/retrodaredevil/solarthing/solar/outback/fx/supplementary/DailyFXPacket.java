package me.retrodaredevil.solarthing.solar.outback.fx.supplementary;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.solar.supplementary.SupplementarySolarPacket;
import me.retrodaredevil.solarthing.solar.supplementary.SupplementarySolarPacketType;
import me.retrodaredevil.solarthing.solar.common.DailyBatteryVoltage;
import me.retrodaredevil.solarthing.solar.outback.fx.*;

import java.util.Collection;
import java.util.Set;

public interface DailyFXPacket extends SupplementarySolarPacket, DailyBatteryVoltage {
	float getInverterKWH();
	float getChargerKWH();
	float getBuyKWH();
	float getSellKWH();

	Collection<Integer> getOperationalModeValues();
	default Set<OperationalMode> getOperationalModes(){ return Modes.getActiveModes(OperationalMode.class, getOperationalModeValues()); }

	int getErrorModeValue();
	default Set<FXErrorMode> getErrorModes(){ return Modes.getActiveModes(FXErrorMode.class, getErrorModeValue()); }

	int getWarningModeValue();
	default Set<WarningMode> getWarningModes(){ return Modes.getActiveModes(WarningMode.class, getWarningModeValue()); }

	int getMiscValue();
	default Set<MiscMode> getMiscModes(){ return Modes.getActiveModes(MiscMode.class, getMiscValue()); }

	Collection<Integer> getACModeValues();
	default Set<ACMode> getACModes(){ return Modes.getActiveModes(ACMode.class, getACModeValues()); }
}
