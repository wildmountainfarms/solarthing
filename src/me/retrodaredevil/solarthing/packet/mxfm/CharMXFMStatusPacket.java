package me.retrodaredevil.solarthing.packet.mxfm;

import me.retrodaredevil.solarthing.packet.CharSolarPacket;
import me.retrodaredevil.solarthing.packet.PacketType;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;

public class CharMXFMStatusPacket extends CharSolarPacket {

	private int chargerCurrent = 00, pvCurrent = 00, inputVoltage = 000;
	private float dailyKWH = 00.0f, ampChargerCurrent = 0.0f;
	private int auxMode = 00, errorMode = 000, chargerMode = 00;
	private float batteryVoltage = 00.0f;
	private int dailyAH = 0000, chksum = 000;

	private String auxModeName;
	private String errors;
	private String chargerModeName;
	
	public CharMXFMStatusPacket(char[] chars) throws CheckSumException, ParsePacketAsciiDecimalDigitException {
		super(chars, PacketType.MXFM_STATUS);
		try{
			init(chars);
		} catch(NumberFormatException ex){
			throw new ParsePacketAsciiDecimalDigitException(ex.getMessage(), charString);
		}
	}
	private void init(char[] chars) throws CheckSumException, NumberFormatException{
		// Start of Status Page
		address = ((int) chars[1]) - 65; // if it is "A" then address would be 0
		int addressChksum = address + 17; // if "A" -> 0 -> ascii 17 (device control 1) weird but that's how it is.
		// , UNUSED UNUSED ,
		int chargerCurrentTens = toInt(chars[6]); // 00 to 99
		int chargerCurrentOnes = toInt(chars[7]);
		// ,
		int pvCurrentTens = toInt(chars[9]); // 00 to 99
		int pvCurrentOnes = toInt(chars[10]);
		// ,
		int inputVoltageHundreds = toInt(chars[12]); // 000 to 255 (pv panel voltage)
		int inputVoltageTens = toInt(chars[13]);
		int inputVoltageOnes = toInt(chars[14]);
		// ,
		int dailyKWHTens = toInt(chars[16]); // 000 to 99.9
		int dailyKWHOnes = toInt(chars[17]);
		int dailyKWHTenths = toInt(chars[18]);
		// , 0
		int ampChargerCurrentTenths = toInt(chars[21]); // 0 to .9
		// ,
		int auxModeTens = toInt(chars[23]); // 0 to 99
		int auxModeOnes = toInt(chars[24]);
		// ,
		int errorModeHundreds = toInt(chars[26]); // 0 to 255
		int errorModeTens = toInt(chars[27]);
		int errorModeOnes = toInt(chars[28]);
		// ,
		int chargerModeTens = toInt(chars[30]); // 0 to 99
		int chargerModeOnes = toInt(chars[31]);
		// ,
		int batteryVoltageTens = toInt(chars[33]); // 0 to 99.9
		int batteryVoltageOnes = toInt(chars[34]);
		int batteryVoltageTenths = toInt(chars[35]);
		// ,
		int dailyAHThousands; // should be 0 to 2000
		int dailyAHHundreds;
		int dailyAHTens;
		int dailyAHOnes;
		char char41 = chars[40];
		if(Character.isDigit(char41)){ // then this must use all four places
			dailyAHThousands = toInt(chars[37]);
			dailyAHHundreds = toInt(chars[38]);
			dailyAHTens = toInt(chars[39]);
			dailyAHOnes = toInt(chars[40]);
			// , UNUSED UNUSED
		} else { // we must be on old firmware
			System.out.println("We must be on old firmware. 41st char(index=40): '" + char41 + "'");
			char char37 = chars[36];
			if(Character.isDigit(char37)){
				// if we get inside this if statement, we know the packet is incorrect and data may be incorrect
				System.err.println("Yep we are definitely outdated. 37nth char (index=36): " + char37);
			}
			dailyAHThousands = 0;
			dailyAHHundreds = toInt(chars[37]);
			dailyAHTens = toInt(chars[38]);
			dailyAHOnes = toInt(chars[39]);
			if(dailyAHHundreds != 0 || dailyAHTens != 0 || dailyAHOnes != 0){
				System.err.println("Even though we are on old firmware, dailyAH isn't 0.");
			}
			// , UNUSED UNUSED UNUSED
		}

		int chksumHundreds = toInt(chars[45]);
		int chksumTens = toInt(chars[46]);
		int chksumOnes = toInt(chars[47]);
		// CARRIAGE RETURN

		int calculatedChksum = addressChksum + chargerCurrentTens + chargerCurrentOnes + pvCurrentTens + pvCurrentOnes +
				inputVoltageHundreds + inputVoltageTens + inputVoltageOnes + dailyKWHTens + dailyKWHOnes + dailyKWHTenths +
				ampChargerCurrentTenths + auxModeTens + auxModeOnes + errorModeHundreds + errorModeTens + errorModeOnes +
				chargerModeTens + chargerModeOnes + batteryVoltageTens + batteryVoltageOnes + batteryVoltageTenths +
				dailyAHThousands + dailyAHHundreds + dailyAHTens + dailyAHOnes;

		chksum = chksumHundreds * 100 + chksumTens * 10 + chksumOnes;

		if(chksum != calculatedChksum){
			throw new CheckSumException(chksum, calculatedChksum, new String(chars));
		}

		chargerCurrent = chargerCurrentTens * 10 + chargerCurrentOnes;
		pvCurrent = pvCurrentTens * 10 + pvCurrentOnes;
		inputVoltage = inputVoltageHundreds * 100 + inputVoltageTens * 10 + inputVoltageOnes;
		
		dailyKWH = dailyKWHTens * 10 + dailyKWHOnes + (dailyKWHTenths / 10.0f);
		ampChargerCurrent = ampChargerCurrentTenths / 10.0f;
		
		auxMode = auxModeTens * 10 + auxModeOnes;
		errorMode = errorModeHundreds * 100 + errorModeTens * 10 + errorModeOnes;
		chargerMode = chargerModeTens * 10 + chargerModeOnes;
		
		batteryVoltage = batteryVoltageTens * 10 + batteryVoltageOnes + (batteryVoltageTenths / 10.0f);

		dailyAH = dailyAHThousands * 1000 + dailyAHHundreds * 100 + dailyAHTens * 10 + dailyAHOnes; // will be 9999 if MX60

		// ==== Aux Mode stuff ====
		AuxMode auxModeObject = AuxMode.getAuxMode(auxMode);
		auxModeName = auxModeObject.toString();

		// ==== Error Mode stuff ====
		StringBuilder errorBuilder = new StringBuilder();
		for(MXFMErrorMode mode : MXFMErrorMode.values()){
			if(mode.isActive(errorMode)){
				errorBuilder.append(mode.toString());
				errorBuilder.append(',');
			}
		}
		errors = errorBuilder.toString();

		// ==== Charge Mode stuff ====
		ChargeMode mode = ChargeMode.getMode(chargerMode);
		chargerModeName = mode.toString();

	}

//	private static String getPrintValue(char c){
//		if(c == PacketCreator49.START){
//			return "start";
//		} else if(c == PacketCreator49.END){
//			return "end";
//		} else if(c == PacketCreator49.NULL_CHAR){
//			return "NUL";
//		}
//		return c + "";
//	}

	public enum AuxMode {
		UNKNOWN(-1, "unknown"),
		DISABLED(0, "disabled"),
		DIVERSION(1, "Diversion"),
		REMOTE(2, "Remote"),
		MANUAL(3, "Manual"),
		VENT_FAN(4, "Vent Fan"),
		PV_TRIGGER(5, "PV Trigger"),
		FLOAT(6, "Float"),
		ERROR_OUTPUT(7, "ERROR Output"),
		NIGHT_LIGHT(8, "Night Light"),
		PWM_DIVERSION(9, "PWM Diversion"),
		LOW_BATTERY(10, "Low Battery");

		private int value;
		private String name;
		AuxMode(int value, String name){
			this.value = value;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public static AuxMode getAuxMode(int auxMode){
			for(AuxMode mode : values()){
				if(mode.value == auxMode){
					return mode;
				}
			}
			return UNKNOWN;
		}
	}
	public enum MXFMErrorMode {
		SHORTED_BATTERY_SENSOR(32, "Shorted Battery Sensor"),
		TOO_HOT(64, "Too Hot"),
		HIGH_VOC(128, "High VOC");

		private int value;
		private String name;
		MXFMErrorMode(int value, String name){
			this.value = value;
			this.name = name;
		}
		public boolean isActive(int errorMode){
			return (value & errorMode) != 0;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	public enum ChargeMode {
		UNKNOWN(-1, "unknown"),
		SILENT(0, "Silent"),
		FLOAT(1, "Float"),
		BULK(2, "Bulk"),
		ABSORB(3, "Absorb"),
		EQ(4, "EQ");

		private int value;
		private String name;
		ChargeMode(int value, String name){
			this.value = value;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
		public static ChargeMode getMode(int chargerMode){
			for(ChargeMode mode : values()){
				if(mode.value == chargerMode){
					return mode;
				}
			}
			return UNKNOWN;
		}
	}

}
