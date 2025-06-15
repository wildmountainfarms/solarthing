package me.retrodaredevil.solarthing.program.subprogram.run;

import me.retrodaredevil.io.modbus.ModbusTimeoutException;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.program.modbus.MutableAddressModbusSlave;
import me.retrodaredevil.solarthing.solar.renogy.RoverBatteryType;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;
import me.retrodaredevil.solarthing.solar.renogy.rover.LoadWorkingMode;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.StreetLight;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.RoverModbusConstants;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E02D;
import me.retrodaredevil.solarthing.util.StringUtil;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Scanner;

@UtilityClass
public final class RoverSetupProgram {
	private RoverSetupProgram(){ throw new UnsupportedOperationException(); }

	public static int startRoverSetup(@Nullable MutableAddressModbusSlave slave, RoverReadTable read, RoverWriteTable write, Runnable reloadCache, Runnable reloadIO){
		System.out.println("Starting rover setup! This is deprecated and will be removed in a future version!.");
		Scanner scanner = new Scanner(System.in, Charset.defaultCharset());
		while (scanner.hasNextLine()) {
			String command = scanner.nextLine();
			String[] split = StringUtil.terminalSplit(command);
			handleSplit(split, slave, read, write);
		}
		return 0;
	}
	private static void handleSplit(String[] split, @Nullable MutableAddressModbusSlave slave, RoverReadTable read, RoverWriteTable write) {
		switch (split.length) {
			case 0 -> {
			}
			case 1 -> {
				// display data
				String request = split[0].toLowerCase(Locale.ENGLISH);
				switch (request) {
					case "maxvoltage" -> System.out.println(read.getMaxVoltage().getModeName());
					case "ratedchargingcurrent", "ratedcurrent" -> System.out.println(read.getRatedChargingCurrentValue());
					case "rateddischargingcurrent" -> System.out.println(read.getRatedDischargingCurrentValue());
					case "producttype" -> System.out.println(read.getProductType().getModeName());
					case "productmodel" -> System.out.println(read.getProductModel());
					case "softwareversion" -> System.out.println(read.getSoftwareVersion());
					case "hardwareversion" -> System.out.println(read.getHardwareVersion());
					case "productserialnumber", "serialnumber", "serial" -> System.out.println(read.getProductSerialNumber());
					case "controllerdeviceaddress", "deviceaddress", "address" -> System.out.println(read.getControllerDeviceAddress());
					case "protocolversion", "protocolversionvalue" -> System.out.println(read.getProtocolVersionValue());
					case "id", "uniqueid", "idcode", "uniqueidcode", "uniqueidentificationcode" -> System.out.println(read.getUniqueIdentificationCode());
					case "batterycapacitysoc", "soc", "percent" -> System.out.println(read.getBatteryCapacitySOC());
					case "batteryvoltage" -> System.out.println(read.getBatteryVoltage());
					case "chargingcurrent" -> System.out.println(read.getChargingCurrent());
					case "controllertemperature" -> System.out.println(read.getControllerTemperatureCelsius());
					case "batterytemperature" -> System.out.println(read.getBatteryTemperatureCelsius());
					case "loadvoltage", "loadvoltageraw", "generatorvoltageraw" -> System.out.println(read.getLoadVoltageRaw());
					case "loadcurrent", "loadcurrentraw", "generatorcurrentraw" -> System.out.println(read.getLoadCurrentRaw());
					case "loadpower", "loadpowerraw", "generatorpowerraw" -> System.out.println(read.getLoadPowerRaw());
					case "inputvoltage", "pvvoltage", "solarvoltage" -> System.out.println(read.getPVVoltage());
					case "pvcurrent", "solarcurrent" -> System.out.println(read.getPVCurrent());
					case "chargingpower" -> System.out.println(read.getChargingPower());
					case "dailyminbatteryvoltage", "dailyminbattery" -> System.out.println(read.getDailyMinBatteryVoltage());
					case "dailymaxbatteryvoltage", "dailymaxbattery" -> System.out.println(read.getDailyMaxBatteryVoltage());
					case "dailymaxchargingcurrent" -> System.out.println(read.getDailyMaxChargingCurrent());
					case "dailymaxdischargingcurrent" -> System.out.println(read.getDailyMaxDischargingCurrent());
					case "dailymaxchargingpower" -> System.out.println(read.getDailyMaxChargingPower());
					case "dailymaxdischargingpower" -> System.out.println(read.getDailyMaxDischargingPower());
					case "dailyah", "dailyahcharging" -> System.out.println(read.getDailyAH());
					case "dailyahdischarging" -> System.out.println(read.getDailyAHDischarging());
					case "dailykwh" -> System.out.println(read.getDailyKWH());
					case "dailykwhconsumption" -> System.out.println(read.getDailyKWHConsumption());
					case "operatingdayscount", "operatingdays" -> System.out.println(read.getOperatingDaysCount());
					case "batteryoverdischargescount", "batteryoverdischarge" -> System.out.println(read.getBatteryOverDischargesCount());
					case "batteryfullchargescount", "batteryfullcharges" -> System.out.println(read.getBatteryFullChargesCount());
					case "ahcount", "ahchargingcount", "ahcharging" -> System.out.println(read.getChargingAmpHoursOfBatteryCount());
					case "ahdischargingcount", "ahdischarging" -> System.out.println(read.getDischargingAmpHoursOfBatteryCount());
					case "cumulativekwh" -> System.out.println(read.getCumulativeKWH());
					case "cumulativekwhcunsumption" -> System.out.println(read.getCumulativeKWHConsumption());
					case "streetlightvalue" -> System.out.println(read.getRawStreetLightValue());
					case "streetlight" -> System.out.println(read.getStreetLightStatus().getModeName());
					case "streetlightbrightness", "brightness" -> System.out.println(read.getStreetLightBrightnessPercent());
					case "chargingstate" -> System.out.println(read.getChargingMode().getModeName());
					case "errormode", "errors" -> System.out.println(read.getErrorsString());
					case "chargingcurrentsettingraw" -> System.out.println(read.getChargingCurrentSettingRaw());
					case "chargingcurrentsetting" -> System.out.println(read.getChargingCurrentSetting());
					case "nominalbatterycapacity", "batterycapacity", "batteryah" -> System.out.println(read.getNominalBatteryCapacity());
					case "systemvoltagesetting", "systemvoltage" -> System.out.println(read.getSystemVoltageSetting().getModeName());
					case "recognizedvoltage" -> System.out.println(read.getRecognizedVoltage().getModeName());
					case "batterytype" -> System.out.println(read.getBatteryType().getModeName());
					case "overvoltagethresholdraw" -> System.out.println(read.getOverVoltageThresholdRaw());
					case "chargingvoltagelimitraw" -> System.out.println(read.getChargingVoltageLimitRaw());
					case "equalizingchargingvoltageraw" -> System.out.println(read.getEqualizingChargingVoltageRaw());
					case "boostchargingvoltageraw" -> System.out.println(read.getBoostChargingVoltageRaw());
					case "floatingchargingvoltageraw" -> System.out.println(read.getFloatingChargingVoltageRaw());
					case "boostchargingrecoveryvoltageraw" -> System.out.println(read.getBoostChargingRecoveryVoltageRaw());
					case "overdischargerecoveryvoltageraw" -> System.out.println(read.getOverDischargeRecoveryVoltageRaw());
					case "undervoltagewarninglevelraw" -> System.out.println(read.getUnderVoltageWarningLevelRaw());
					case "overdischargevoltageraw" -> System.out.println(read.getOverDischargeVoltageRaw());
					case "discharginglimitvoltageraw" -> System.out.println(read.getDischargingLimitVoltageRaw());
					case "endofchargesoc" -> System.out.println(read.getEndOfChargeSOC());
					case "endofdischargesoc" -> System.out.println(read.getEndOfDischargeSOC());
					case "overdischargetimedelayseconds" -> System.out.println(read.getOverDischargeTimeDelaySeconds());
					case "equalizingchargingtimeminutes" -> System.out.println(read.getEqualizingChargingTimeMinutes());
					case "boostchargingtimeminutes" -> System.out.println(read.getBoostChargingTimeMinutes());
					case "equalizingchargingintervaldays" -> System.out.println(read.getEqualizingChargingIntervalDays());
					case "temperaturecompensationfactor" -> System.out.println(read.getTemperatureCompensationFactor());

					// operating setting stuff
					case "loadworkingmode" -> System.out.println(read.getLoadWorkingMode().getModeName());
					case "lightcontroldelayminutes" -> System.out.println(read.getLightControlDelayMinutes());
					case "lightcontrolvoltage" -> System.out.println(read.getLightControlVoltage());
					case "ledloadcurrentsettingmilliamps" -> System.out.println(read.getLEDLoadCurrentSettingMilliAmps());
					case "specialpowercontrole021" -> System.out.println(read.getSpecialPowerControlE021().getFormattedInfo());

					// sensing stuff
					case "sensingtimedelayseconds" -> System.out.println(read.getSensingTimeDelaySeconds());
					case "ledloadcurrentmilliamps" -> System.out.println(read.getLEDLoadCurrentMilliAmps());
					case "specialpowercontrole02d" -> {
						SpecialPowerControl_E02D specialPowerControl2 = read.getSpecialPowerControlE02D();
						if (specialPowerControl2 == null) {
							System.out.println("null");
						} else {
							System.out.println(specialPowerControl2.getFormattedInfo());
						}
					}
					case "controllerchargingpowersetting" -> System.out.println(read.getControllerChargingPowerSetting());
					case "generatorchargingpowersetting" -> System.out.println(read.getGeneratorChargingPowerSetting());
					default -> System.err.println(request + " is not supported!");
				}
			}
			case 2 -> {
				// set most data
				String toSet = split[1].toLowerCase(Locale.ENGLISH);
				switch (split[0].toLowerCase(Locale.ENGLISH)) {
					case "factoryreset" -> {
						if (toSet.equals("!!")) {
							write.factoryReset();
						} else {
							throw new IllegalArgumentException("You must do 'factoryreset !!' to confirm!");
						}
					}
					case "clearhistory" -> {
						if (toSet.equals("!!")) {
							write.clearHistory();
						} else {
							throw new IllegalArgumentException("You must do 'clearhistory !!' to confirm!");
						}
					}
					case "controllerdeviceaddress", "deviceaddress", "address" -> write.setControllerDeviceAddress(Integer.parseInt(toSet));
					case "streetlight" -> {
						final boolean streetOn;
						if (toSet.equals("on")) {
							streetOn = true;
						} else if (toSet.equals("off")) {
							streetOn = false;
						} else {
							throw new IllegalArgumentException(toSet + " not supported for streetlight on/off");
						}
						write.setStreetLightStatus(streetOn ? StreetLight.ON : StreetLight.OFF);
					}
					case "brightness" -> {
						int brightness = Integer.parseInt(toSet);
						write.setStreetLightBrightnessPercent(brightness);
					}
					case "systemvoltage", "systemvoltagesetting" -> {
						Voltage systemVoltage = parseVoltage(toSet);
						boolean supported = systemVoltage.isSupported(read.getMaxVoltage());
						if (!supported) {
							System.err.println(systemVoltage.getModeName() + " is not supported!");
						} else {
							write.setSystemVoltageSetting(systemVoltage);
						}
					}
					case "batterytype" -> {
						RoverBatteryType batteryType = RoverBatteryType.parseOrNull(toSet);

						if (batteryType != null) {
							write.setBatteryType(batteryType);
							if (batteryType.isUser()) {
								System.err.println("You set one of the user battery types. This will likely not work. You will have to manually change this on your Rover");
							}
						} else {
							System.err.println(toSet + " not supported as a battery type!");
						}
					}
					case "overvoltagethresholdraw" -> write.setOverVoltageThresholdRaw(checkRawVoltage(Integer.parseInt(toSet)));
					case "chargingvoltagelimitraw" -> write.setChargingVoltageLimitRaw(checkRawVoltage(Integer.parseInt(toSet)));
					case "equalizingchargingvoltageraw" -> write.setEqualizingChargingVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
					case "boostchargingvoltageraw" -> write.setBoostChargingVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
					case "floatingchargingvoltageraw" -> write.setFloatingChargingVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
					case "boostchargingrecoveryvoltageraw" -> write.setBoostChargingRecoveryVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
					case "overdischargerecoveryvoltageraw" -> write.setOverDischargeRecoveryVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
					case "undervoltagewarninglevelraw" -> write.setUnderVoltageWarningLevelRaw(checkRawVoltage(Integer.parseInt(toSet)));
					case "discharginglimitvoltageraw" -> write.setDischargingLimitVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));

					// end of charge SOC end of discharge SOC
					case "overdischargetimedelayseconds" -> {
						int overDischargeTimeDelay = Integer.parseInt(toSet);
						if (overDischargeTimeDelay < 0) throw new IllegalArgumentException("Cannot be less than 0");
						if (overDischargeTimeDelay > 120) throw new IllegalArgumentException("Cannot be greater than 120");
						write.setOverDischargeTimeDelaySeconds(overDischargeTimeDelay); // 0 - 120
					}
					case "equalizingchargingtimeminutes" -> write.setEqualizingChargingTimeMinutes(Integer.parseInt(toSet));
					case "boostchargingtimeminutes" -> write.setBoostChargingTimeMinutes(Integer.parseInt(toSet));
					case "equalizingchargingintervaldays" -> write.setEqualizingChargingIntervalDays(Integer.parseInt(toSet));
					case "temperaturecompensationfactor" -> write.setTemperatureCompensationFactor(Integer.parseInt(toSet));

					// operating settings
					case "loadworkingmode" -> {
						final LoadWorkingMode loadWorkingMode = Modes.getActiveMode(LoadWorkingMode.class, Integer.parseInt(toSet));
						write.setLoadWorkingMode(loadWorkingMode);
					}
					case "lightcontroldelayminutes" -> {
						int lightControlDelay = Integer.parseInt(toSet);
						if (lightControlDelay < 0) throw new IllegalArgumentException("cannot be less than 0!");
						if (lightControlDelay > 60) throw new IllegalArgumentException("cannot be greater than 60!");
						write.setLightControlDelayMinutes(lightControlDelay); // 0 - 60
					}
					case "lightcontrolvoltage" -> {
						int lightControlVoltage = Integer.parseInt(toSet);
						if (lightControlVoltage < 1) throw new IllegalArgumentException("cannot be less than 1!");
						if (lightControlVoltage > 40) throw new IllegalArgumentException("cannot be greater than 40!");
						write.setLightControlVoltage(lightControlVoltage);
					}
					case "ledloadcurrentsettingmilliamps" -> {
						int ledLoadCurrentSettingMilliAmps = Integer.parseInt(toSet);
						write.setLEDLoadCurrentSettingMilliAmps(ledLoadCurrentSettingMilliAmps);
					}
					case "specialpowercontrole021raw" -> {
						int spce021Raw = Integer.parseInt(toSet);
						write.setSpecialPowerControlE021Raw(spce021Raw);
					}
					// sensing values
					case "sensingtimedelayseconds" -> {
						int sensingTimeDelaySeconds = Integer.parseInt(toSet);
						write.setSensingTimeDelaySeconds(sensingTimeDelaySeconds);
					}
					case "ledloadcurrentmilliamps" -> {
						int ledLoadCurrentMilliAmps = Integer.parseInt(toSet);
						write.setLEDLoadCurrentMilliAmps(ledLoadCurrentMilliAmps);
					}
					case "specialpowercontrole02draw" -> {
						int spce02dRaw = Integer.parseInt(toSet);
						write.setSpecialPowerControlE02DRaw(spce02dRaw);
					}
					default -> System.err.println("Not supported!");
				}
			}
			case 3 -> {
				if (!split[0].equalsIgnoreCase("scan")) {
					System.err.println("Not supported!");
					break;
				}
				if (slave == null) {
					System.err.println("scan not supported for the current config!");
				} else {
					String lowerBoundString = split[1];
					String upperBoundString = split[2];
					int lower = Integer.parseInt(lowerBoundString);
					int upper = Integer.parseInt(upperBoundString);
					if (lower > upper) {
						System.err.println("lower cannot be bigger than upper!");
					} else if (lower < RoverModbusConstants.MIN_ADDRESS) {
						System.err.println("The lower bound cannot be less than 1!");
					} else if (upper > RoverModbusConstants.MAX_ADDRESS) {
						System.err.println("The upper bound cannot be greater than 247!");
					} else {
						System.out.println("Starting scan. (Will tell you when scan has stopped)");

						int originalAddress = slave.getAddress();
						for (int i = lower; i <= upper; i++) {
							slave.setAddress(i);
							try {
								read.getBatteryVoltage();
								System.out.println("Found on address: " + i + "!");
							} catch (ModbusTimeoutException ignored) {
							}
						}
						slave.setAddress(originalAddress);

						System.out.println("Scan finished!");
					}
				}
			}
			case 1 + 19 -> {
				// set some data
				// For testing: "set17 24 user 170 155 146 144 138 132 126 120 110 105 100 50 5 60 60 30 5"
				if (!split[0].equalsIgnoreCase("bigset")) {
					System.err.println("Not supported!");
					break;
				}
				Voltage systemVoltage = parseVoltage(split[1]);
				boolean supported = systemVoltage.isSupported(read.getMaxVoltage());
				if (!supported) {
					System.err.println(systemVoltage.getModeName() + " is not supported!");
					break;
				} else {
					write.setSystemVoltageSetting(systemVoltage);
				}
				RoverBatteryType batteryType = RoverBatteryType.parseOrNull(split[2]);
				if (batteryType == null) {
					System.out.println("Unsupported battery type: " + split[2]);
					break;
				}
				int[] data = new int[17];
				for (int i = 0; i < data.length; i++) {
					String argument = split[i + 3];
					data[i] = Integer.parseInt(argument);
				}
				write.setBatteryParameters(
						systemVoltage, batteryType,
						data[0], data[1], data[2], data[3],
						data[4], data[5], data[6], data[7],
						data[8], data[9], data[10], data[11],
						data[12], data[13], data[14], data[15],
						data[16]
				);
			}
			default -> System.out.println("This isn't supported!");
		}
	}
	private static int checkRawVoltage(int rawVoltage){
		if(rawVoltage < 70){
			throw new IllegalArgumentException("raw voltage cannot be less than 70!");
		}
		if(rawVoltage > 170){
			throw new IllegalArgumentException("raw voltage cannot be greater than 170!");
		}
		return rawVoltage;
	}
	private static Voltage parseVoltage(String string) {
		Voltage systemVoltage = null;
		try {
			systemVoltage = Modes.getActiveMode(Voltage.class, Integer.parseInt(string), Voltage.AUTO);
		} catch(NumberFormatException ignored){
		}
		if(systemVoltage == null){
			return Voltage.AUTO;
		}
		return systemVoltage;
	}
}
