package me.retrodaredevil.solarthing.solar.outback.fx.charge;

import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;

import static java.util.Objects.requireNonNull;

/**
 * A state machine that can calculate the timer values on the FX that are shown in one of the Mate display screens
 */
public class FXChargingStateHandler {
	private static final float ROUNDING_ERROR = 0.00001f;
	/*
	This class tries to emulate/calculate the timer for the FX's charging stages.

	The implementation varies from model to model and firmware to firmware, but I tried my best.
	The most untested thing here is what happens when AC power is lost, then restored.

	Also, sometimes in the operator's manual for some FX models, the timers accumulated back up instead of just resetting. I'll need to read more to confirm
	If you see something in here that you don't think is correct, please, create an issue at https://github.com/wildmountainfarms/solarthing/issues

	NOTE: The Sell RE Set Point thing is not implemented in here, and will probably break this calculation if used
	 */
	private final FXChargingSettings fxChargingSettings;

	private final ModeTimer rebulkTimer = new ModeTimer(1000 * 90);

	private final ModeTimer absorbTimer;
	private final ModeTimer floatTimer;
	private final ModeTimer equalizeTimer;

	private boolean atAbsorbSetpoint = false;
	private boolean atEqualizeSetpoint = false;
	private boolean atFloatSetpoint = false;

	private OperationalMode previousOperationalMode = null;

	public FXChargingStateHandler(FXChargingSettings fxChargingSettings) {
		this.fxChargingSettings = requireNonNull(fxChargingSettings);
		absorbTimer = new ModeTimer(fxChargingSettings.getAbsorbTimeMillis());
		floatTimer = new ModeTimer(fxChargingSettings.getFloatTimeMillis());
		equalizeTimer = new ModeTimer(fxChargingSettings.getEqualizeTimeMillis());
	}


	public void update(long deltaTimeMillis, FXStatusPacket fx){
		update(deltaTimeMillis, fx.getOperationalMode(), fx.getBatteryVoltage());
	}
	public void update(long deltaTimeMillis, OperationalMode operationalMode, float batteryVoltage){
		OperationalMode previousOperationalMode = this.previousOperationalMode;
		this.previousOperationalMode = operationalMode;
		float stepSize = fxChargingSettings.getStepSize(); // momentary battery jumps are not reported to us, but the FX reacts to them, so we can use this to offset our setpoints to make this more accurate

		float refloatVoltage = fxChargingSettings.getRefloatVoltage();
		if(batteryVoltage <= refloatVoltage + ROUNDING_ERROR){
			floatTimer.resetTimer();
			atFloatSetpoint = false;
		}
		Float rebulkVoltage = fxChargingSettings.getRebulkVoltage();
		if(rebulkVoltage != null && batteryVoltage <= rebulkVoltage + ROUNDING_ERROR){
			rebulkTimer.countDown(deltaTimeMillis);
			if(rebulkTimer.isDone()) {
				absorbTimer.resetTimer();
				floatTimer.resetTimer();
				equalizeTimer.resetTimer();
				atAbsorbSetpoint = false;
				atEqualizeSetpoint = false;
				atFloatSetpoint = false;
			}
		} else {
			rebulkTimer.resetTimer();
		}
		if(fxChargingSettings.isFloatTimerStartedImmediately()){
			if(batteryVoltage + ROUNDING_ERROR + stepSize >= fxChargingSettings.getFloatVoltage()){
				atFloatSetpoint = true;
			}
		}
		if(operationalMode == OperationalMode.CHARGE){
			atEqualizeSetpoint = false;
			if(batteryVoltage + ROUNDING_ERROR + stepSize >= fxChargingSettings.getAbsorbVoltage()){
				atAbsorbSetpoint = true;
			}
		} else if(operationalMode == OperationalMode.EQ){
			atAbsorbSetpoint = false;
			if(batteryVoltage + ROUNDING_ERROR + stepSize >= fxChargingSettings.getEqualizeVoltage()){
				atEqualizeSetpoint = true;
			}
		} else if(operationalMode == OperationalMode.SILENT){
			absorbTimer.resetTimer();
			equalizeTimer.resetTimer();
			atAbsorbSetpoint = false;
			atEqualizeSetpoint = false;
			atFloatSetpoint = false;
		} else if(operationalMode == OperationalMode.FLOAT){
			absorbTimer.resetTimer();
			equalizeTimer.resetTimer();
			atAbsorbSetpoint = false;
			atEqualizeSetpoint = false;
			if(previousOperationalMode == OperationalMode.SILENT){ // there's never an instance where a transition from silent to float should have the float timer counted down any amount
				floatTimer.resetTimer();
			}
			if(batteryVoltage + ROUNDING_ERROR + stepSize >= fxChargingSettings.getFloatVoltage()){
				atFloatSetpoint = true;
			}
		} else { // not charging
			atAbsorbSetpoint = false;
			atEqualizeSetpoint = false;
			atFloatSetpoint = false;
			if(rebulkVoltage == null){ // older FXs do not support continuing cycle through AC loss
				absorbTimer.resetTimer();
				floatTimer.resetTimer();
				equalizeTimer.resetTimer();
			}
		}
		if(atAbsorbSetpoint){
			absorbTimer.countDown(deltaTimeMillis);
		}
		if(atEqualizeSetpoint){
			equalizeTimer.countDown(deltaTimeMillis);
		}
		if(atFloatSetpoint){
			floatTimer.countDown(deltaTimeMillis);
		}
	}
	public FXChargingMode getMode(){
		OperationalMode operationalMode = this.previousOperationalMode;
		if(operationalMode == null){
			return null;
		} else if(operationalMode == OperationalMode.CHARGE){
			if(atAbsorbSetpoint){
				return FXChargingMode.ABSORB;
			}
			return FXChargingMode.BULK_TO_ABSORB;
		} else if(operationalMode == OperationalMode.EQ){
			if(atEqualizeSetpoint){
				return FXChargingMode.EQ;
			}
			return FXChargingMode.BULK_TO_EQ;
		} else if(operationalMode == OperationalMode.FLOAT){
			if(atFloatSetpoint){
				return FXChargingMode.FLOAT;
			}
			return FXChargingMode.REFLOAT;
		} else if(operationalMode == OperationalMode.SILENT){
			return FXChargingMode.SILENT;
		}
		return null;
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
