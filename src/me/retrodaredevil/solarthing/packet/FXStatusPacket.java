package me.retrodaredevil.solarthing.packet;

import lombok.Getter;

public class FXStatusPacket extends CharSolarPacket{

	@Getter
	private int inverterAddress =  0, inverterCurrent =  00, chargerCurrent =  00, buyCurrent =  00, inputVoltage =  000, outputVoltage =  000, 
		sellCurrent =  00, operatingMode =  00, errorMode =  000, acMode =  00;
	@Getter
	private float batteryVoltage =  00.0f;
	@Getter
	private int misc =  000, warningMode =  000, chksum =  000;

	private String operatingModeName;
	private String errors;
	private String acModeName;
	private String miscModes;
	private String warnings;

	private final PacketType packetType = PacketType.FX_STATUS;
//
	
	
	public FXStatusPacket(char[] chars) {
		super(chars);
		init(super.chars);
	}
	private void init(char[] chars){
		// start of status page
		int inverterAddressOnes = toInt(chars[1]);
		// ,
		int inverterCurrentTens = toInt(chars[3]);
		int inverterCurrentOnes = toInt(chars[4]);
		// ,
		int chargerCurrentTens = toInt(chars[6]);
		int chargerCurrentOnes = toInt(chars[7]);
		// ,
		int buyCurrentTens = toInt(chars[9]);
		int buyCurrentOnes = toInt(chars[10]);
		// ,
		int inputVoltageHundreds = toInt(chars[12]);
		int inputVoltageTens = toInt(chars[13]);
		int inputVoltageOnes = toInt(chars[14]);
		// ,
		int outputVoltageHundreds = toInt(chars[16]);
		int outputVoltageTens = toInt(chars[17]);
		int outputVoltageOnes = toInt(chars[18]);
		// ,
		int sellCurrentTens = toInt(chars[20]); // 00 to 99 - AC current the FX is delivering from batteries to AC input
		int sellCurrentOnes = toInt(chars[21]);
		// ,
		int operatingModeTens = toInt(chars[23]); // 00 to 99
		int operatingModeOnes = toInt(chars[24]);
		// ,
		int errorModeHundreds = toInt(chars[26]); // 000 to 255
		int errorModeTens = toInt(chars[27]);
		int errorModeOnes = toInt(chars[28]);
		// ,
		int acModeTens = toInt(chars[30]); // 00 to 99 - status of AC input. 00:NO AC, 01:AC Drop, 02: AC Use
		int acModeOnes = toInt(chars[31]);
		// ,
		int batteryVoltageTens = toInt(chars[33]); // 000 to 999 -> XX.X
		int batteryVoltageOnes = toInt(chars[34]);
		int batteryVoltageTenths = toInt(chars[35]);
		// ,
		int miscHundreds = toInt(chars[37]); // 000 to 255
		int miscTens = toInt(chars[38]);
		int miscOnes = toInt(chars[39]);
		// ,
		int warningModeHundreds = toInt(chars[41]);
		int warningModeTens = toInt(chars[42]);
		int warningModeOnes = toInt(chars[43]);
		// ,
		int chksumHundreds = toInt(chars[45]);
		int chksumTens = toInt(chars[46]);
		int chksumOnes = toInt(chars[47]);

		int calculatedChksum = inverterAddressOnes + inverterCurrentTens + inverterCurrentOnes + chargerCurrentTens + chargerCurrentOnes +
				buyCurrentTens + buyCurrentOnes + inputVoltageHundreds + inputVoltageTens + inputVoltageOnes +
				outputVoltageHundreds + outputVoltageTens + outputVoltageOnes + sellCurrentTens + sellCurrentOnes +
				operatingModeTens + operatingModeOnes + errorModeHundreds + errorModeTens + errorModeOnes + acModeTens + acModeOnes +
				batteryVoltageTens + batteryVoltageOnes + batteryVoltageTenths + miscHundreds + miscTens + miscOnes + warningModeHundreds + warningModeTens + warningModeOnes;

		chksum = chksumHundreds * 100 + chksumTens * 10 + chksumOnes;

		if(chksum != calculatedChksum){
			throw new IllegalStateException("The chksum wasn't correct! Something must have gone wrong. chars: '" + new String(chars) + "'" +
					" chksum: " + chksum + " calculated chksum: " + calculatedChksum);
		}

		// set values
		inverterAddress = inverterAddressOnes;
		inverterCurrent = inverterCurrentTens * 10 + inverterCurrentOnes;
		chargerCurrent = chargerCurrentTens * 10 + chargerCurrentOnes;
		buyCurrent = buyCurrentTens * 10 + buyCurrentOnes;
		inputVoltage = inputVoltageHundreds * 100 + inputVoltageTens * 10 + inputVoltageOnes;
		outputVoltage = outputVoltageHundreds * 100 + outputVoltageTens * 10 + outputVoltageOnes;
		
		sellCurrent = sellCurrentTens * 10 + sellCurrentOnes;
		operatingMode = operatingModeTens * 10 + operatingModeOnes;
		errorMode = errorModeHundreds * 100 + errorModeTens * 10 + errorModeOnes; // high middle and low
		acMode = acModeTens * 10 + acModeOnes;  // high and low
		
		batteryVoltage = batteryVoltageTens * 10 + batteryVoltageOnes + (batteryVoltageTenths / 10.0f);
		
		misc = miscHundreds * 100 + miscTens * 10 + miscOnes;
		warningMode = warningModeHundreds * 100 + warningModeTens * 10 + warningModeOnes;
		// chksum set above

		// Operational Mode Stuff ====
		OperationalMode operationalMode = OperationalMode.getMode(operatingMode);
		operatingModeName = operationalMode.toString();

		// ==== Error mode stuff ====
		StringBuilder errorBuilder = new StringBuilder();
		for(ErrorMode mode : ErrorMode.values()){
			if (mode.isActive(errorMode)) {
				errorBuilder.append(mode.toString());
				errorBuilder.append(',');
			}
		}
		errors = errorBuilder.toString();

		// ==== AC Mode Stuff ====
		ACMode acModeObject = ACMode.getACMode(acMode);
		acModeName = acModeObject.toString();

		// ==== Misc Stuff ====
		miscModes = "";
		if(MiscMode.FX_230V_UNIT.isActive(misc)){
			System.out.println("230V unit is active! misc: " + misc);
			inputVoltage *= 2;
			outputVoltage *= 2;

			inverterCurrent /= 2;
			chargerCurrent /= 2;
			buyCurrent /= 2;
			sellCurrent /= 2;
			miscModes += MiscMode.FX_230V_UNIT.toString() + ", ";
		}
		if(MiscMode.AUX_OUTPUT_ON.isActive(misc)){
			System.out.println("AUX output is on.");
			miscModes += MiscMode.AUX_OUTPUT_ON.toString();
		}

		// ==== Warning Mode stuff ====
		StringBuilder warningBuilder = new StringBuilder();
		for(WarningMode mode : WarningMode.values()){
			if(mode.isActive(warningMode)){
				System.out.println("WarningMode: " + mode.toString() + " is active!! Possibly very bad");
				warningBuilder.append(mode.toString());
				warningBuilder.append(',');
			}
		}
		warnings = warningBuilder.toString();

	}
	@Override
	public int getPortNumber() {
		return inverterAddress;
	}
	@Override
	public PacketType getPacketType() {
		return packetType;
	}
	public enum OperationalMode{ // one must be active
		UNKNOWN(-1, "unknown"),
		INV_OFF(0, "Inv Off"),
		SEARCH(1, "Search"),
		INV_ON(2, "Inv On"),
		CHARGE(3, "Charge"),
		SILENT(4, "Silent"),
		FLOAT(5, "Float"),
		EQ(6, "EQ"),
		CHARGER_OFF(7, "Charger Off"),
		SUPPORT(8, "Support"),
		SELL_ENABLED(9, "Sell Enabled"),
		PASS_THRU(10, "Pass Thru"),

		FX_ERROR(90, "FX Error"),
		AGS_ERROR(91, "AGS Error"),
		COM_ERROR(92, "Com Error");

		private int value;
		private String name;
		OperationalMode(int value, String name){
			this.value = value;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
		public static OperationalMode getMode(int operatingMode){
			for(OperationalMode mode : values()){
				if(mode.value == operatingMode){
					return mode;
				}
			}
			return UNKNOWN;
		}
	}
	public enum ErrorMode { // multiple can be active (or 0)
		LOW_VAC_OUTPUT(1, "Low VAC output"),
		STACKING_ERROR(2, "Stacking Error"),
		OVER_TEMP(4, "Over Temp."),
		LOW_BATTERY(8, "Low Battery"),
		PHASE_LOSS(16, "Phase Loss"),
		HIGH_BATTERY(32, "High Battery"),
		SHORTED_OUTPUT(64, "Shorted output"),
		BACK_FEED(128, "Back feed");
		private int value;
		private String name;
		ErrorMode(int value, String name){
			this.value = value;
			this.name = name;
		}
		public boolean isActive(int errorMode){
			return (errorMode & value) != 0;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	public enum ACMode{ // one must be active
		NO_AC(0, "No AC"),
		AC_DROP(1, "AC Drop"),
		AC_USE(2, "AC Use"),
		UNKNOWN(-1, "UNKNOWN");

		private int value;
		private String name;
		ACMode(int value, String name){
			this.value = value;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
		public static ACMode getACMode(int value){
			for(ACMode mode : values()){
				if(mode.value == value){
					return mode;
				}
			}
			return UNKNOWN;
		}
	}
	public enum MiscMode{ // multiple can be active (or 0)
		FX_230V_UNIT(1, "230V unit - voltages * 2 and currents / 2"),
		AUX_OUTPUT_ON(128, "AUX output ON");

		private int value;
		private String name;
		MiscMode(int value, String name){
			this.value = value;
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
		public boolean isActive(int miscValue){
			return (miscValue & value) != 0;
		}
	}
	public enum WarningMode { // multiple can be active (or 0)
		AC_INPUT_FREQ_HIGH(1, "AC Input Freq High"),
		AC_INPUT_FREQ_LOW(2, "AC Input Freq Low"),
		INPUT_VAC_HIGH(4, "Input VAC High"),
		INPUT_VAC_LOW(8, "Input VAC Low"),
		BUY_AMPS_GT_INPUT_SIZE(16, "Buy Amps > Input size"),
		TEMP_SENSOR_FAILED(32, "Temp Sensor failed"),
		COMM_ERROR(64, "Comm Error"),
		FAN_FAILURE(128, "Fan Failure");

		private int value;
		private String name;
		WarningMode(int value, String name){
			this.value = value;
			this.name = name;
		}
		public boolean isActive(int warningMode){
			return (warningMode & value) != 0;
		}

		@Override
		public String toString() {
			return name;
		}
	}

}
