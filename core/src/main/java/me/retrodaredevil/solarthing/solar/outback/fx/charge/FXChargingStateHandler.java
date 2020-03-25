package me.retrodaredevil.solarthing.solar.outback.fx.charge;

import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;

public class FXChargingStateHandler {
	private final FXChargingSettings fxChargingSettings;

	private final ModeTimer absorbTimer;
	private final ModeTimer floatTimer;
	private final ModeTimer equalizeTimer;

	private FXChargingMode mode = FXChargingMode.BULK;

	public FXChargingStateHandler(FXChargingSettings fxChargingSettings) {
		this.fxChargingSettings = fxChargingSettings;
		absorbTimer = new ModeTimer(fxChargingSettings.getAbsorbTimeMillis());
		floatTimer = new ModeTimer(fxChargingSettings.getFloatTimeMillis());
		equalizeTimer = new ModeTimer(fxChargingSettings.getEqualizeTimeMillis());
	}


	private void checkTimers(float batteryVoltage, boolean usingAC){
		if(mode == FXChargingMode.BULK){
			Float rebulkVoltage = fxChargingSettings.getRebulkVoltage();
			if((rebulkVoltage == null && !usingAC) || (rebulkVoltage != null && batteryVoltage <= rebulkVoltage)) { // reset all
				absorbTimer.resetTimer();
				equalizeTimer.resetTimer();
				floatTimer.resetTimer();
			}
		} else if(mode == FXChargingMode.REFLOAT || mode == FXChargingMode.FLOAT){
			float refloatVoltage = fxChargingSettings.getRefloatVoltage();
			if(batteryVoltage <= refloatVoltage){
				floatTimer.resetTimer();
				if(mode == FXChargingMode.FLOAT){
					mode = FXChargingMode.REFLOAT;
				}
			}
		}
	}
	public void update(long deltaTimeMillis, FXStatusPacket fx){
		update(deltaTimeMillis, fx.getACMode() == ACMode.AC_USE, fx.getOperationalMode() == OperationalMode.EQ, fx.getBatteryVoltage());
	}
	public void update(long deltaTimeMillis, boolean usingAC, boolean eq, float batteryVoltage){
		if (usingAC) { // AC USE
			if(!eq && (mode == FXChargingMode.BULK || mode == FXChargingMode.ABSORB_OR_EQ) && fxChargingSettings.isAbsorbZero()){
				mode = FXChargingMode.REFLOAT; // skip bulk and absorb if absorb time is zero
			}
			if(mode == FXChargingMode.BULK){
				float setpointVoltage = eq ? fxChargingSettings.getEqualizeVoltage() : fxChargingSettings.getAbsorbVoltage();
				if(batteryVoltage >= setpointVoltage){
					mode = FXChargingMode.ABSORB_OR_EQ;
				}
			} else if(mode == FXChargingMode.SILENT){
				float refloatVoltage = fxChargingSettings.getRefloatVoltage();
				if(batteryVoltage <= refloatVoltage){
					mode = FXChargingMode.REFLOAT;
				}
			} else if(mode == FXChargingMode.REFLOAT){
				float floatVoltage = fxChargingSettings.getFloatVoltage();
				if(batteryVoltage >= floatVoltage){
					mode = FXChargingMode.FLOAT;
				}
			}
			if(mode == FXChargingMode.ABSORB_OR_EQ){
				if(eq){
					equalizeTimer.countDown(deltaTimeMillis);
					if(equalizeTimer.isDone()){
						mode = FXChargingMode.SILENT;
					}
				} else {
					absorbTimer.countDown(deltaTimeMillis);
					if(absorbTimer.isDone()){
						mode = FXChargingMode.SILENT;
					}
				}
			} else if(mode == FXChargingMode.FLOAT){
				floatTimer.countDown(deltaTimeMillis);
				if(floatTimer.isDone()){
					mode = FXChargingMode.SILENT;
				}
			}
		} else { // AC Drop or No AC
			if(mode == FXChargingMode.ABSORB_OR_EQ){
				mode = FXChargingMode.BULK;
			} else if(mode == FXChargingMode.FLOAT){
				mode = FXChargingMode.REFLOAT;
			}
		}
		checkTimers(batteryVoltage, usingAC);
	}
	public FXChargingMode getMode(){
		return mode;
	}
	public long getRemainingAbsorbTimeMillis(){
		return absorbTimer.getRemainingTimeMillis();
	}
	public long getRemainingFloatTimeMillis(){
		return floatTimer.getRemainingTimeMillis();
	}
	public long getRemainingEqualizeTimeMillis(){
		return equalizeTimer.getRemainingTimeMillis();
	}
}
