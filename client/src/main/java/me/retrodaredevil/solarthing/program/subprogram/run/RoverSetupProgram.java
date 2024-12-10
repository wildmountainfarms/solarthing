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
		switch(split.length){
			case 0:
				break;
			case 1:
				// display data
				String request = split[0].toLowerCase(Locale.ENGLISH);
				switch(request){
					case "maxvoltage":
						System.out.println(read.getMaxVoltage().getModeName());
						break;
					case "ratedchargingcurrent": case "ratedcurrent":
						System.out.println(read.getRatedChargingCurrentValue());
						break;
					case "rateddischargingcurrent":
						System.out.println(read.getRatedDischargingCurrentValue());
						break;
					case "producttype":
						System.out.println(read.getProductType().getModeName());
						break;
					case "productmodel":
						System.out.println(read.getProductModel());
						break;
					case "softwareversion":
						System.out.println(read.getSoftwareVersion());
						break;
					case "hardwareversion":
						System.out.println(read.getHardwareVersion());
						break;
					case "productserialnumber": case "serialnumber": case "serial":
						System.out.println(read.getProductSerialNumber());
						break;
					case "controllerdeviceaddress": case "deviceaddress": case "address":
						System.out.println(read.getControllerDeviceAddress());
						break;
					case "protocolversion": case "protocolversionvalue":
						System.out.println(read.getProtocolVersionValue());
						break;
					case "id": case "uniqueid": case "idcode": case "uniqueidcode": case "uniqueidentificationcode":
						System.out.println(read.getUniqueIdentificationCode());
						break;
					case "batterycapacitysoc": case "soc": case "percent":
						System.out.println(read.getBatteryCapacitySOC());
						break;
					case "batteryvoltage":
						System.out.println(read.getBatteryVoltage());
						break;
					case "chargingcurrent":
						System.out.println(read.getChargingCurrent());
						break;
					case "controllertemperature":
						System.out.println(read.getControllerTemperatureCelsius());
						break;
					case "batterytemperature":
						System.out.println(read.getBatteryTemperatureCelsius());
						break;
					case "loadvoltage": case "loadvoltageraw": case "generatorvoltageraw":
						System.out.println(read.getLoadVoltageRaw());
						break;
					case "loadcurrent": case "loadcurrentraw": case "generatorcurrentraw":
						System.out.println(read.getLoadCurrentRaw());
						break;
					case "loadpower": case "loadpowerraw": case "generatorpowerraw":
						System.out.println(read.getLoadPowerRaw());
						break;
					case "inputvoltage": case "pvvoltage": case "solarvoltage":
						System.out.println(read.getPVVoltage());
						break;
					case "pvcurrent": case "solarcurrent":
						System.out.println(read.getPVCurrent());
						break;
					case "chargingpower":
						System.out.println(read.getChargingPower());
						break;
					case "dailyminbatteryvoltage": case "dailyminbattery":
						System.out.println(read.getDailyMinBatteryVoltage());
						break;
					case "dailymaxbatteryvoltage": case "dailymaxbattery":
						System.out.println(read.getDailyMaxBatteryVoltage());
						break;
					case "dailymaxchargingcurrent":
						System.out.println(read.getDailyMaxChargingCurrent());
						break;
					case "dailymaxdischargingcurrent":
						System.out.println(read.getDailyMaxDischargingCurrent());
						break;
					case "dailymaxchargingpower":
						System.out.println(read.getDailyMaxChargingPower());
						break;
					case "dailymaxdischargingpower":
						System.out.println(read.getDailyMaxDischargingPower());
						break;
					case "dailyah": case "dailyahcharging":
						System.out.println(read.getDailyAH());
						break;
					case "dailyahdischarging":
						System.out.println(read.getDailyAHDischarging());
						break;
					case "dailykwh":
						System.out.println(read.getDailyKWH());
						break;
					case "dailykwhconsumption":
						System.out.println(read.getDailyKWHConsumption());
						break;
					case "operatingdayscount": case "operatingdays":
						System.out.println(read.getOperatingDaysCount());
						break;
					case "batteryoverdischargescount": case "batteryoverdischarge":
						System.out.println(read.getBatteryOverDischargesCount());
						break;
					case "batteryfullchargescount": case "batteryfullcharges":
						System.out.println(read.getBatteryFullChargesCount());
						break;
					case "ahcount": case "ahchargingcount": case "ahcharging":
						System.out.println(read.getChargingAmpHoursOfBatteryCount());
						break;
					case "ahdischargingcount": case "ahdischarging":
						System.out.println(read.getDischargingAmpHoursOfBatteryCount());
						break;
					case "cumulativekwh":
						System.out.println(read.getCumulativeKWH());
						break;
					case "cumulativekwhcunsumption":
						System.out.println(read.getCumulativeKWHConsumption());
						break;
					case "streetlightvalue":
						System.out.println(read.getRawStreetLightValue());
						break;
					case "streetlight":
						System.out.println(read.getStreetLightStatus().getModeName());
						break;
					case "streetlightbrightness": case "brightness":
						System.out.println(read.getStreetLightBrightnessPercent());
						break;
					case "chargingstate":
						System.out.println(read.getChargingMode().getModeName());
						break;
					case "errormode": case "errors":
						System.out.println(read.getErrorsString());
						break;
					case "chargingcurrentsettingraw":
						System.out.println(read.getChargingCurrentSettingRaw());
						break;
					case "chargingcurrentsetting":
						System.out.println(read.getChargingCurrentSetting());
						break;
					case "nominalbatterycapacity": case "batterycapacity": case "batteryah":
						System.out.println(read.getNominalBatteryCapacity());
						break;
					case "systemvoltagesetting": case "systemvoltage":
						System.out.println(read.getSystemVoltageSetting().getModeName());
						break;
					case "recognizedvoltage":
						System.out.println(read.getRecognizedVoltage().getModeName());
						break;
					case "batterytype":
						System.out.println(read.getBatteryType().getModeName());
						break;
					case "overvoltagethresholdraw":
						System.out.println(read.getOverVoltageThresholdRaw());
						break;
					case "chargingvoltagelimitraw":
						System.out.println(read.getChargingVoltageLimitRaw());
						break;
					case "equalizingchargingvoltageraw":
						System.out.println(read.getEqualizingChargingVoltageRaw());
						break;
					case "boostchargingvoltageraw":
						System.out.println(read.getBoostChargingVoltageRaw());
						break;
					case "floatingchargingvoltageraw":
						System.out.println(read.getFloatingChargingVoltageRaw());
						break;
					case "boostchargingrecoveryvoltageraw":
						System.out.println(read.getBoostChargingRecoveryVoltageRaw());
						break;
					case "overdischargerecoveryvoltageraw":
						System.out.println(read.getOverDischargeRecoveryVoltageRaw());
						break;
					case "undervoltagewarninglevelraw":
						System.out.println(read.getUnderVoltageWarningLevelRaw());
						break;
					case "overdischargevoltageraw":
						System.out.println(read.getOverDischargeVoltageRaw());
						break;
					case "discharginglimitvoltageraw":
						System.out.println(read.getDischargingLimitVoltageRaw());
						break;

					case "endofchargesoc":
						System.out.println(read.getEndOfChargeSOC());
						break;
					case "endofdischargesoc":
						System.out.println(read.getEndOfDischargeSOC());
						break;
					case "overdischargetimedelayseconds":
						System.out.println(read.getOverDischargeTimeDelaySeconds());
						break;
					case "equalizingchargingtimeminutes":
						System.out.println(read.getEqualizingChargingTimeMinutes());
						break;
					case "boostchargingtimeminutes":
						System.out.println(read.getBoostChargingTimeMinutes());
						break;
					case "equalizingchargingintervaldays":
						System.out.println(read.getEqualizingChargingIntervalDays());
						break;
					case "temperaturecompensationfactor":
						System.out.println(read.getTemperatureCompensationFactor());
						break;
					// operating setting stuff
					case "loadworkingmode":
						System.out.println(read.getLoadWorkingMode().getModeName());
						break;
					case "lightcontroldelayminutes":
						System.out.println(read.getLightControlDelayMinutes());
						break;
					case "lightcontrolvoltage":
						System.out.println(read.getLightControlVoltage());
						break;
					case "ledloadcurrentsettingmilliamps":
						System.out.println(read.getLEDLoadCurrentSettingMilliAmps());
						break;
					case "specialpowercontrole021":
						System.out.println(read.getSpecialPowerControlE021().getFormattedInfo());
						break;
					// sensing stuff
					case "sensingtimedelayseconds":
						System.out.println(read.getSensingTimeDelaySeconds());
						break;
					case "ledloadcurrentmilliamps":
						System.out.println(read.getLEDLoadCurrentMilliAmps());
						break;
					case "specialpowercontrole02d":
						SpecialPowerControl_E02D specialPowerControl2 = read.getSpecialPowerControlE02D();
						if (specialPowerControl2 == null) {
							System.out.println("null");
						} else {
							System.out.println(specialPowerControl2.getFormattedInfo());
						}
						break;
					case "controllerchargingpowersetting":
						System.out.println(read.getControllerChargingPowerSetting());
						break;
					case "generatorchargingpowersetting":
						System.out.println(read.getGeneratorChargingPowerSetting());
						break;
					default:
						System.err.println(request + " is not supported!");
						break;
				}
				break;
			case 2:
				// set most data
				String toSet = split[1].toLowerCase(Locale.ENGLISH);
				switch(split[0].toLowerCase(Locale.ENGLISH)) {
					case "factoryreset":
						if(toSet.equals("!!")){
							write.factoryReset();
						} else {
							throw new IllegalArgumentException("You must do 'factoryreset !!' to confirm!");
						}
						break;
					case "clearhistory":
						if(toSet.equals("!!")){
							write.clearHistory();
						} else {
							throw new IllegalArgumentException("You must do 'clearhistory !!' to confirm!");
						}
						break;
					case "controllerdeviceaddress": case "deviceaddress": case "address":
						write.setControllerDeviceAddress(Integer.parseInt(toSet));
						break;
					case "streetlight": // on/off
						final boolean streetOn;
						if(toSet.equals("on")) {
							streetOn = true;
						} else if(toSet.equals("off")){
							streetOn = false;
						} else {
							throw new IllegalArgumentException(toSet + " not supported for streetlight on/off");
						}
						write.setStreetLightStatus(streetOn ? StreetLight.ON : StreetLight.OFF);
						break;
					case "brightness": // 0-100
						int brightness = Integer.parseInt(toSet);
						write.setStreetLightBrightnessPercent(brightness);
						break;
					case "systemvoltage": case "systemvoltagesetting":
						Voltage systemVoltage = parseVoltage(toSet);
						boolean supported = systemVoltage.isSupported(read.getMaxVoltage());
						if(!supported){
							System.err.println(systemVoltage.getModeName() + " is not supported!");
						} else {
							write.setSystemVoltageSetting(systemVoltage);
						}
						break;
					case "batterytype":
						RoverBatteryType batteryType = RoverBatteryType.parseOrNull(toSet);

						if(batteryType != null) {
							write.setBatteryType(batteryType);
							if (batteryType.isUser()) {
								System.err.println("You set one of the user battery types. This will likely not work. You will have to manually change this on your Rover");
							}
						} else {
							System.err.println(toSet + " not supported as a battery type!");
						}
						break;
					case "overvoltagethresholdraw":
						write.setOverVoltageThresholdRaw(checkRawVoltage(Integer.parseInt(toSet)));
						break;
					case "chargingvoltagelimitraw":
						write.setChargingVoltageLimitRaw(checkRawVoltage(Integer.parseInt(toSet)));
						break;
					case "equalizingchargingvoltageraw":
						write.setEqualizingChargingVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
						break;
					case "boostchargingvoltageraw":
						write.setBoostChargingVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
						break;
					case "floatingchargingvoltageraw":
						write.setFloatingChargingVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
						break;
					case "boostchargingrecoveryvoltageraw":
						write.setBoostChargingRecoveryVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
						break;
					case "overdischargerecoveryvoltageraw":
						write.setOverDischargeRecoveryVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
						break;
					case "undervoltagewarninglevelraw":
						write.setUnderVoltageWarningLevelRaw(checkRawVoltage(Integer.parseInt(toSet)));
						break;
					case "discharginglimitvoltageraw":
						write.setDischargingLimitVoltageRaw(checkRawVoltage(Integer.parseInt(toSet)));
						break;
					// end of charge SOC end of discharge SOC
					case "overdischargetimedelayseconds":
						int overDischargeTimeDelay = Integer.parseInt(toSet);
						if(overDischargeTimeDelay < 0) throw new IllegalArgumentException("Cannot be less than 0");
						if(overDischargeTimeDelay > 120) throw new IllegalArgumentException("Cannot be greater than 120");
						write.setOverDischargeTimeDelaySeconds(overDischargeTimeDelay); // 0 - 120
						break;
					case "equalizingchargingtimeminutes":
						write.setEqualizingChargingTimeMinutes(Integer.parseInt(toSet));
						break;
					case "boostchargingtimeminutes":
						write.setBoostChargingTimeMinutes(Integer.parseInt(toSet));
						break;
					case "equalizingchargingintervaldays":
						write.setEqualizingChargingIntervalDays(Integer.parseInt(toSet));
						break;
					case "temperaturecompensationfactor":
						write.setTemperatureCompensationFactor(Integer.parseInt(toSet));
						break;
					// operating settings
					case "loadworkingmode":
						final LoadWorkingMode loadWorkingMode = Modes.getActiveMode(LoadWorkingMode.class, Integer.parseInt(toSet));
						write.setLoadWorkingMode(loadWorkingMode);
						break;
					case "lightcontroldelayminutes":
						int lightControlDelay = Integer.parseInt(toSet);
						if(lightControlDelay < 0) throw new IllegalArgumentException("cannot be less than 0!");
						if(lightControlDelay > 60) throw new IllegalArgumentException("cannot be greater than 60!");
						write.setLightControlDelayMinutes(lightControlDelay); // 0 - 60
						break;
					case "lightcontrolvoltage":
						int lightControlVoltage = Integer.parseInt(toSet);
						if(lightControlVoltage < 1) throw new IllegalArgumentException("cannot be less than 1!");
						if(lightControlVoltage > 40) throw new IllegalArgumentException("cannot be greater than 40!");
						write.setLightControlVoltage(lightControlVoltage);
						break;
					case "ledloadcurrentsettingmilliamps":
						int ledLoadCurrentSettingMilliAmps = Integer.parseInt(toSet);
						write.setLEDLoadCurrentSettingMilliAmps(ledLoadCurrentSettingMilliAmps);
						break;
					case "specialpowercontrole021raw":
						int spce021Raw = Integer.parseInt(toSet);
						write.setSpecialPowerControlE021Raw(spce021Raw);
						break;
					// sensing values
					case "sensingtimedelayseconds":
						int sensingTimeDelaySeconds = Integer.parseInt(toSet);
						write.setSensingTimeDelaySeconds(sensingTimeDelaySeconds);
						break;
					case "ledloadcurrentmilliamps":
						int ledLoadCurrentMilliAmps = Integer.parseInt(toSet);
						write.setLEDLoadCurrentMilliAmps(ledLoadCurrentMilliAmps);
						break;
					case "specialpowercontrole02draw":
						int spce02dRaw = Integer.parseInt(toSet);
						write.setSpecialPowerControlE02DRaw(spce02dRaw);
						break;
					default:
						System.err.println("Not supported!");
						break;
				}
				break;
			case 3:
				if(!split[0].equalsIgnoreCase("scan")){
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
				break;
			case 1 + 19:
				// set some data
				// For testing: "set17 24 user 170 155 146 144 138 132 126 120 110 105 100 50 5 60 60 30 5"
				if(!split[0].equalsIgnoreCase("bigset")){
					System.err.println("Not supported!");
					break;
				}
				Voltage systemVoltage = parseVoltage(split[1]);
				boolean supported = systemVoltage.isSupported(read.getMaxVoltage());
				if(!supported){
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
				for(int i = 0; i < data.length; i++){
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
				break;
			default:
				System.out.println("This isn't supported!");
				break;
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
