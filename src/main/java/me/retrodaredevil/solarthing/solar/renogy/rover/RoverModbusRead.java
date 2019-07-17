package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.modbus.ModbusRead;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.renogy.Version;

import java.text.DecimalFormat;
import java.text.Format;

public class RoverModbusRead implements RoverReadTable {
	private static final Format TEN_THOUSANDTHS_FORMAT = new DecimalFormat("00.0000");
	private static final Format HUNDREDTHS_FORMAT = new DecimalFormat("00.00");
	private static final Format TENTHS_FORMAT = new DecimalFormat("00.0");
	
	private final ModbusRead modbus;
	
	public RoverModbusRead(ModbusRead modbus) {
		this.modbus = modbus;
	}
	
	@Override
	public int getMaxVoltageValue() {
		return modbus.readRegisterUpper8Bits(0x000A);
	}
	
	@Override
	public int getRatedChargingCurrentValue() {
		return modbus.readRegisterLower8Bits(0x000A);
	}
	
	@Override
	public int getRatedDischargingCurrentValue() {
		return modbus.readRegisterUpper8Bits(0x000B);
	}
	
	@Override
	public int getProductTypeValue() {
		return modbus.readRegisterLower8Bits(0x000B);
	}
	
	@Override
	public byte[] getProductModelValue() {
		return modbus.readRegistersToByteArray(0x000C, 8);
	}
	
	@Override
	public int getSoftwareVersionValue() {
		return modbus.readRegisterAndNext(0x0014);
	}
	
	@Override
	public int getHardwareVersionValue() {
		return modbus.readRegisterAndNext(0x0016);
	}
	
	@Override
	public int getProductSerialNumber() {
		return modbus.readRegisterAndNext(0x0018);
	}
	
	@Override
	public int getControllerDeviceAddress() {
		return modbus.readRegister(0x001A);
	}
	
	@Override
	public int getBatteryCapacitySOC() {
		return modbus.readRegister(0x0100);
	}
	
	@Override
	public Float getChargerCurrent() {
		return modbus.readRegister(0x0102) / 100.0F;
	}
	
	@Override
	public float getBatteryVoltage() {
		int raw = modbus.readRegister(0x0101);
        return raw / 10.0F;
	}
	
	@Override
	public String getBatteryVoltageString() {
		return TENTHS_FORMAT.format(getBatteryVoltage());
	}
	
	@Override
	public String getChargerCurrentString() {
		int raw = modbus.readRegister(0x0102);
		return HUNDREDTHS_FORMAT.format(raw / 100.0F);
	}
	
	@Override
	public int getControllerTemperature() {
		return modbus.readRegisterUpper8Bits(0x0103);
	}
	
	@Override
	public int getBatteryTemperature() {
		return modbus.readRegisterLower8Bits(0x0103);
	}
	
	@Override
	public float getLoadVoltage() {
		return modbus.readRegister(0x0104) / 10.0F;
	}
	
	@Override
	public float getLoadCurrent() {
		return modbus.readRegister(0x0105) / 100.0F;
	}
	
	@Override
	public int getLoadPower() {
		return modbus.readRegister(0x0106);
	}
	
	@Override
	public Float getInputVoltage() { // pv voltage/solar panel voltage
		return modbus.readRegister(0x0107) / 10.0F;
	}
	
	@Override
	public Float getPVCurrent() {
		return modbus.readRegister(0x0108) / 100.0F;
	}
	
	@Override
	public Integer getChargingPower() {
		return modbus.readRegister(0x0109);
	}
	
	@Override
	public BatteryVoltage getDailyMinBatteryVoltage() {
		return BatteryVoltage.createTenthsBatteryVoltage(modbus.readRegister(0x010B));
	}
	@Override
	public BatteryVoltage getDailyMaxBatteryVoltage() {
		return BatteryVoltage.createTenthsBatteryVoltage(modbus.readRegister(0x010C));
	}
	
	@Override
	public float getDailyMaxChargerCurrent() {
		return modbus.readRegister(0x010D) / 100.0F;
	}
	
	@Override
	public float getDailyMaxDischargingCurrent() {
		return modbus.readRegister(0x010E) / 100.0F;
	}
	
	@Override
	public int getDailyMaxChargingPower() {
		return modbus.readRegister(0x010F);
	}
	
	@Override
	public int getDailyMaxDischargingPower() {
		return modbus.readRegister(0x0110);
	}
	
	@Override
	public int getDailyAH() {
		return modbus.readRegister(0x0111);
	}
	
	@Override
	public int getDailyAHDischarging() {
		return modbus.readRegister(0x0112);
	}
	
	@Override
	public float getDailyKWH() {
		return modbus.readRegister(0x0113) / 10_000.0F;
	}
	
	@Override
	public String getDailyKWHString() {
		return TEN_THOUSANDTHS_FORMAT.format(getDailyKWH());
	}
	
	@Override
	public float getDailyKWHConsumption() {
		return modbus.readRegister(0x0114) / 10_000.0F;
	}
	
	@Override
	public int getOperatingDaysCount() {
		return modbus.readRegister(0x0115);
	}
	
	@Override
	public int getBatteryOverDischargesCount() {
		return modbus.readRegister(0x0116);
	}
	
	@Override
	public int getBatteryFullChargesCount() {
		return modbus.readRegister(0x0117);
	}
	
	@Override
	public int getChargingAmpHoursOfBatteryCount() {
		return modbus.readRegisterAndNext(0x0118);
	}
	
	@Override
	public int getDischargingAmpHoursOfBatteryCount() {
		return modbus.readRegisterAndNext(0x011A);
	}
	
	@Override
	public float getCumulativeKWH() {
		return modbus.readRegisterAndNext(0x011C) / 10_000.0F;
	}
	
	@Override
	public float getCumulativeKWHConsumption() {
		return modbus.readRegisterAndNext(0x011E) / 10_000.0F;
	}
	
	@Override
	public int getStreetLightValue() {
		return modbus.readRegisterUpper8Bits(0x0120);
	}
	
	@Override
	public int getChargingStateValue() {
		return modbus.readRegisterLower8Bits(0x0120);
	}
	
	@Override
	public int getErrorMode() {
		return modbus.readRegisterAndNext(0x0121);
	}
	
	@Override
	public int getNominalBatteryCapacity() {
		return modbus.readRegister(0xE002);
	}
	
	@Override
	public int getSystemVoltageSettingValue() {
		return modbus.readRegisterUpper8Bits(0xE003);
	}
	
	@Override
	public int getRecognizedVoltageValue() {
		return modbus.readRegisterLower8Bits(0xE003);
	}
	
	@Override
	public int getBatteryTypeValue() {
		return modbus.readRegister(0xE004);
	}
	
	@Override
	public int getOverVoltageThresholdRaw() {
		return modbus.readRegister(0xE005);
	}
	
	@Override
	public int getChargingVoltageLimitRaw() {
		return modbus.readRegister(0xE006);
	}
	
	@Override
	public int getEqualizingChargingVoltageRaw() {
		return modbus.readRegister(0xE007);
	}
	
	@Override
	public int getBoostChargingVoltageRaw() {
		return modbus.readRegister(0xE008);
	}
	
	@Override
	public int getFloatingChargingVoltageRaw() {
		return modbus.readRegister(0xE009);
	}
	
	@Override
	public int getBoostChargingRecoveryVoltageRaw() {
		return modbus.readRegister(0xE00A);
	}
	
	@Override
	public int getOverDischargeRecoveryVoltageRaw() {
		return modbus.readRegister(0xE00B);
	}
	
	@Override
	public int getUnderVoltageWarningLevelRaw() {
		return modbus.readRegister(0xE00C);
	}
	
	@Override
	public int getOverDischargeVoltageRaw() {
		return modbus.readRegister(0xE00D);
	}
	
	@Override
	public int getDischargingLimitVoltageRaw() {
		return modbus.readRegister(0xE00E);
	}
	
	@Override
	public int getEndOfChargeSOC() {
		return modbus.readRegisterUpper8Bits(0xE00F);
	}
	
	@Override
	public int getEndOfDischargeSOC() {
		return modbus.readRegisterLower8Bits(0xE00F);
	}
	
	@Override
	public int getOverDischargeTimeDelaySeconds() {
		return modbus.readRegister(0xE010);
	}
	
	@Override
	public int getEqualizingChargingTimeRaw() {
		return modbus.readRegister(0xE011);
	}
	
	@Override
	public int getBoostChargingTimeRaw() {
		return modbus.readRegister(0xE012);
	}
	
	@Override
	public int getEqualizingChargingIntervalRaw() {
		return modbus.readRegister(0xE013);
	}
	
	@Override
	public int getTemperatureCompensationFactorRaw() {
		return modbus.readRegister(0xE14);
	}
	
	@Override
	public OperatingStage getStage1() {
		return new OperatingStage(modbus.readRegister(0xE015), modbus.readRegister(0xE016));
	}
	@Override
	public OperatingStage getStage2() {
		return new OperatingStage(modbus.readRegister(0xE017), modbus.readRegister(0xE018));
	}
	@Override
	public OperatingStage getStage3() {
		return new OperatingStage(modbus.readRegister(0xE019), modbus.readRegister(0xE01A));
	}
	@Override
	public OperatingStage getMorningOn() {
		return new OperatingStage(modbus.readRegister(0xE01B), modbus.readRegister(0xE01C));
	}
	
	@Override
	public int getLoadWorkingModeValue() {
		return modbus.readRegister(0xE01D);
	}
	
	@Override
	public int getLightControlDelayMinutes() {
		return modbus.readRegister(0xE01E);
	}
	
	@Override
	public int getLightControlVoltage() {
		return modbus.readRegister(0xE01F);
	}
	
	@Override
	public int getLEDLoadCurrentSettingRaw() {
		return modbus.readRegister(0xE020);
	}
	
	@Override
	public int getSpecialPowerControlE021Raw() {
		return modbus.readRegister(0xE021);
	}
	
	@Override
	public PowerSensing getPowerSensing1() {
		return new PowerSensing(modbus.readRegister(0xE022), modbus.readRegister(0xE023), modbus.readRegister(0xE024));
	}
	@Override
	public PowerSensing getPowerSensing2() {
		return new PowerSensing(modbus.readRegister(0xE025), modbus.readRegister(0xE026), modbus.readRegister(0xE027));
	}
	@Override
	public PowerSensing getPowerSensing3() {
		return new PowerSensing(modbus.readRegister(0xE028), modbus.readRegister(0xE029), modbus.readRegister(0xE02A));
	}
	
	@Override
	public int getSensingTimeDelayRaw() {
		return modbus.readRegister(0xE02B);
	}
	
	@Override
	public int getLEDLoadCurrentRaw() {
		return modbus.readRegister(0xE02C);
	}
	
	@Override
	public int getSpecialPowerControlE02DRaw() {
		return modbus.readRegister(0xE02D);
	}
}
