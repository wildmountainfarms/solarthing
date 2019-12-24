package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;
import me.retrodaredevil.solarthing.solar.renogy.rover.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class RoverSetupProgram {
	private RoverSetupProgram(){ throw new UnsupportedOperationException(); }
	
	public static void startRoverSetup(RoverReadTable read, RoverWriteTable write){
		Scanner scanner = new Scanner(System.in);
		loop: while (scanner.hasNextLine()) {
			String command = scanner.nextLine();
			String[] commentSplit = command.split("#");
			final String[] split;
			if(commentSplit.length == 0){
				split = new String[0];
			} else {
				List<String> splitList = new ArrayList<>(Arrays.asList(commentSplit[0].split(" ")));
				while(splitList.contains("")){
					splitList.remove("");
				}
				split = splitList.toArray(new String[0]);
			}
			switch(split.length){
				case 0:
					continue loop;
				case 1:
					// display data
					String request = split[0].toLowerCase();
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
							System.out.println(read.getControllerTemperature());
							break;
						case "batterytemperature":
							System.out.println(read.getBatteryTemperature());
							break;
						case "loadvoltage":
							System.out.println(read.getLoadVoltage());
							break;
						case "loadcurrent":
							System.out.println(read.getLoadCurrent());
							break;
						case "loadpower":
							System.out.println(read.getLoadPower());
							break;
						case "inputvoltage": case "pvvoltage": case "solarlvoltage":
							System.out.println(read.getInputVoltage());
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
							System.out.println(Modes.toString(RoverErrorMode.class, read.getErrorModeValue()));
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
						case "ledloadcurrentmilliamps":
							System.out.println(read.getLEDLoadCurrentMilliAmps());
							break;
						case "specialpowercontrole02d":
							System.out.println(read.getSpecialPowerControlE02D().getFormattedInfo());
							break;
						default:
							System.err.println(request + " is not supported!");
							break;
					}
					break;
				case 2:
					// set most data
					String toSet = split[1].toLowerCase();
					switch(split[0].toLowerCase()) {
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
							Voltage systemVoltage = null;
							try {
								systemVoltage = Modes.getActiveMode(Voltage.class, Integer.parseInt(toSet), Voltage.AUTO);
							} catch(NumberFormatException ex){
							}
							if(systemVoltage == null){
								systemVoltage = Voltage.AUTO;
							}
							boolean supported = systemVoltage.isSupported(read.getMaxVoltage());
							if(!supported){
								System.err.println(systemVoltage.getModeName() + " is not supported!");
							} else {
								write.setSystemVoltageSetting(systemVoltage);
							}
							break;
						case "batterytype":
							final BatteryType batteryType;
							switch(toSet){
								case "open": batteryType = BatteryType.OPEN; break;
								case "sealed": batteryType = BatteryType.SEALED; break;
								case "gel": batteryType = BatteryType.GEL; break;
								case "lithium": batteryType = BatteryType.LITHIUM; break;
								case "self-customized": case "custom": case "customized": batteryType = BatteryType.SELF_CUSTOMIZED; break;
								default: System.err.println(toSet + " not supported as a battery type!"); batteryType = null; break;
							}
							if(batteryType != null) {
								write.setBatteryType(batteryType);
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
				case 1 + 17:
					// set some data
					// For testing: "set17 170 155 146 144 138 132 126 120 110 105 100 50 5 60 60 30 5"
					if(!split[0].equalsIgnoreCase("set17")){
						System.err.println("Not supported!");
						break;
					}
					int[] data = new int[17];
					for(int i = 0; i < data.length; i++){
						String argument = split[i + 1];
						data[i] = Integer.parseInt(argument);
					}
					write.setVoltageSetPoints(
						data[0], data[1], data[2], data[3],
						data[4], data[5], data[6], data[7],
						data[8], data[9], data[10], data[11],
						data[12], data[13], data[14], data[15],
						data[16]
					);
					break;
				default:
					System.out.println("This isn't supported!");
					continue loop;
			}
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
}
