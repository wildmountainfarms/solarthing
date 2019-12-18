package me.retrodaredevil.solarthing.solar.outback.fx.supplementary;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.DailyBatteryVoltage;
import me.retrodaredevil.solarthing.solar.outback.fx.*;

import java.util.Collection;
import java.util.Set;

public interface DailyFXPacket extends DailyBatteryVoltage {
	float getInverterKWH();
	float getChargerKWH();
	float getBuyKWH();
	float getSellKWH();

	Collection<Integer> getOperationalModeValues();
	default Set<OperationalMode> getOperationalModes(){ return Modes.getActiveModes(OperationalMode.class, getOperationalModeValues()); }

	int getAllError();
	default Set<FXErrorMode> getErrorModes(){ return Modes.getActiveModes(FXErrorMode.class, getAllError()); }

	int getAllWarning();
	default Set<WarningMode> getWarningModes(){ return Modes.getActiveModes(WarningMode.class, getAllWarning()); }

	int getAllMisc();
	default Set<MiscMode> getMiscModes(){ return Modes.getActiveModes(MiscMode.class, getAllMisc()); }

	Collection<Integer> getAllACModeValues();
	default Set<ACMode> getACModes(){ return Modes.getActiveModes(ACMode.class, getAllACModeValues()); }
}
