package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;
import me.retrodaredevil.solarthing.solar.renogy.rover.LoadWorkingMode;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.StreetLight;

import java.util.Scanner;

public final class RoverSetupProgram {
	private RoverSetupProgram(){ throw new UnsupportedOperationException(); }
	
	public static void startRoverSetup(RoverReadTable read, RoverWriteTable write){
		Scanner scanner = new Scanner(System.in);
		loop: while (scanner.hasNextLine()) {
			String command = scanner.nextLine();
			String[] split = command.split(" ");
			switch(split.length){
				case 0:
					continue loop;
				case 1:
					// display data
					String request = split[0].toLowerCase();
					switch(request){
						case "batteryvoltage":
							System.out.println(read.getBatteryVoltage());
							break;
						case "maxvoltage":
							System.out.println(read.getMaxVoltage().getModeName());
							break;
						case "producttype":
							System.out.println(read.getProductType().getModeName());
							break;
						case "controllerdeviceaddress": case "deviceaddress": case "address":
							System.out.println(read.getControllerDeviceAddress());
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
						case "systemvoltage":
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
							
							break;
						case "specialpowercontrole021raw":
							
							break;
						// sensing values
						case "sensingtimedelayseconds":
							
							break;
						case "ledloadcurrentmilliamps":
							
							break;
						case "specialpowercontrole02draw":
							
							break;
						default:
							System.err.println("Not supported!");
							break;
					}
					break;
				case 3:
					// set some data
					
					break;
				default:
					System.out.println("Only a maximum of 3 arguments are allowed!");
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
