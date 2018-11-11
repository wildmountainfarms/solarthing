package me.retrodaredevil.solarthing.packet.mxfm;

import me.retrodaredevil.solarthing.packet.PacketType;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;

import static me.retrodaredevil.solarthing.util.ParseUtil.toInt;

public class ImmutableMXFMStatusPacket implements MXFMStatusPacket {
	private final PacketType packetType = PacketType.MXFM_STATUS;

	private final int address;

	private final int chargerCurrent, pvCurrent, inputVoltage;

	private final float dailyKWH;
	private final String dailyKWHString;

	private final float ampChargerCurrent;
	private final String ampChargerCurrentString;

	private final int auxMode, errorMode, chargerMode;

	private final float batteryVoltage;
	private final String batteryVoltageString;

	private final int dailyAH, chksum;

	private final String auxModeName;
	private final String errors;
	private final String chargerModeName;

	public ImmutableMXFMStatusPacket(int address, int chargerCurrent, int pvCurrent, int inputVoltage,
									 float dailyKWH, String dailyKWHString,
									 float ampChargerCurrent, String ampChargerCurrentString,
									 int auxMode, int errorMode, int chargerMode,
									 float batteryVoltage, String batteryVoltageString,
									 int dailyAH, int chksum, String auxModeName, String errors, String chargerModeName) {
		this.address = address;
		this.chargerCurrent = chargerCurrent;
		this.pvCurrent = pvCurrent;
		this.inputVoltage = inputVoltage;
		this.dailyKWH = dailyKWH;
		this.dailyKWHString = dailyKWHString;
		this.ampChargerCurrent = ampChargerCurrent;
		this.ampChargerCurrentString = ampChargerCurrentString;
		this.auxMode = auxMode;
		this.errorMode = errorMode;
		this.chargerMode = chargerMode;
		this.batteryVoltage = batteryVoltage;
		this.batteryVoltageString = batteryVoltageString;
		this.dailyAH = dailyAH;
		this.chksum = chksum;
		this.auxModeName = auxModeName;
		this.errors = errors;
		this.chargerModeName = chargerModeName;
	}
	public ImmutableMXFMStatusPacket(char[] chars) throws CheckSumException, ParsePacketAsciiDecimalDigitException {
		// Start of Status Page
		address = ((int) chars[1]) - 65; // if it is "A" then address would be 0
		final int addressChksum = address + 17; // if "A" -> 0 -> ascii 17 (device control 1) weird but that's how it is.
		// , UNUSED UNUSED ,
		final int chargerCurrentTens = toInt(chars, 6); // 00 to 99
		final int chargerCurrentOnes = toInt(chars, 7);
		// ,
		final int pvCurrentTens = toInt(chars, 9); // 00 to 99
		final int pvCurrentOnes = toInt(chars, 10);
		// ,
		final int inputVoltageHundreds = toInt(chars, 12); // 000 to 255 (pv panel voltage)
		final int inputVoltageTens = toInt(chars, 13);
		final int inputVoltageOnes = toInt(chars, 14);
		// ,
		final int dailyKWHTens = toInt(chars, 16); // 000 to 99.9
		final int dailyKWHOnes = toInt(chars, 17);
		final int dailyKWHTenths = toInt(chars, 18);
		// , 0
		final int ampChargerCurrentTenths = toInt(chars, 21); // 0 to .9
		// ,
		final int auxModeTens = toInt(chars, 23); // 0 to 99
		final int auxModeOnes = toInt(chars, 24);
		// ,
		final int errorModeHundreds = toInt(chars, 26); // 0 to 255
		final int errorModeTens = toInt(chars, 27);
		final int errorModeOnes = toInt(chars, 28);
		// ,
		final int chargerModeTens = toInt(chars, 30); // 0 to 99
		final int chargerModeOnes = toInt(chars, 31);
		// ,
		final int batteryVoltageTens = toInt(chars, 33); // 0 to 99.9
		final int batteryVoltageOnes = toInt(chars, 34);
		final int batteryVoltageTenths = toInt(chars, 35);
		// ,
		final int dailyAHThousands; // should be 0 to 2000
		final int dailyAHHundreds;
		final int dailyAHTens;
		final int dailyAHOnes;
		final char char41 = chars[40];
		if(Character.isDigit(char41)){ // then this must use all four places
			dailyAHThousands = toInt(chars, 37);
			dailyAHHundreds = toInt(chars, 38);
			dailyAHTens = toInt(chars, 39);
			dailyAHOnes = toInt(chars, 40);
			// , UNUSED UNUSED
		} else { // we must be on old firmware
			System.out.println("We must be on old firmware. 41st char(index=40): '" + char41 + "'");
			char char37 = chars[36];
			if(Character.isDigit(char37)){
				// if we get inside this if statement, we know the packet is incorrect and data may be incorrect
				System.err.println("Yep we are definitely outdated. 37nth char (index=36): " + char37);
			}
			dailyAHThousands = 0;
			dailyAHHundreds = toInt(chars, 37);
			dailyAHTens = toInt(chars, 38);
			dailyAHOnes = toInt(chars, 39);
			if(dailyAHHundreds != 0 || dailyAHTens != 0 || dailyAHOnes != 0){
				System.err.println("Even though we are on old firmware, dailyAH isn't 0.");
			}
			// , UNUSED UNUSED UNUSED
		}

		final int chksumHundreds = toInt(chars, 45);
		final int chksumTens = toInt(chars, 46);
		final int chksumOnes = toInt(chars, 47);
		// CARRIAGE RETURN

		final int calculatedChksum = addressChksum + chargerCurrentTens + chargerCurrentOnes + pvCurrentTens + pvCurrentOnes +
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
		dailyKWHString = dailyKWHTens + "" + dailyKWHOnes + "." + dailyKWHTenths;
		ampChargerCurrent = ampChargerCurrentTenths / 10.0f;
		ampChargerCurrentString = "0." + ampChargerCurrentTenths;

		auxMode = auxModeTens * 10 + auxModeOnes;
		errorMode = errorModeHundreds * 100 + errorModeTens * 10 + errorModeOnes;
		chargerMode = chargerModeTens * 10 + chargerModeOnes;

		batteryVoltage = batteryVoltageTens * 10 + batteryVoltageOnes + (batteryVoltageTenths / 10.0f);
		batteryVoltageString = batteryVoltageTens + "" + batteryVoltageOnes + "." + batteryVoltageTenths;

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
		ChargerMode mode = ChargerMode.getMode(chargerMode);
		chargerModeName = mode.toString();

	}

	@Override
	public String getAuxModeName() {
        return auxModeName;
	}

	@Override
	public String getErrorsString() {
        return errors;
	}

	@Override
	public String getChargerModeName() {
        return chargerModeName;
	}

	@Override
	public PacketType getPacketType() {
        return packetType;
	}

	@Override
	public int getAddress() {
        return address;
	}

	@Override
	public int getChargerCurrent() {
        return chargerCurrent;
	}

	@Override
	public int getPVCurrent() {
        return pvCurrent;
	}

	@Override
	public int getInputVoltage() {
        return inputVoltage;
	}

	@Override
	public float getDailyKWH() {
        return dailyKWH;
	}

	@Override
	public String getDailyKWHString() {
        return dailyKWHString;
	}

	@Override
	public float getAmpChargerCurrent() {
        return ampChargerCurrent;
	}

	@Override
	public String getAmpChargerCurrentString() {
        return ampChargerCurrentString;
	}

	@Override
	public int getAuxMode() {
        return auxMode;
	}

	@Override
	public int getErrorMode() {
        return errorMode;
	}

	@Override
	public int getChargerMode() {
        return chargerMode;
	}

	@Override
	public float getBatteryVoltage() {
        return batteryVoltage;
	}

	@Override
	public String getBatteryVoltageString() {
        return batteryVoltageString;
	}

	@Override
	public int getDailyAH() {
        return dailyAH;
	}

	@Override
	public int getChksum() {
        return chksum;
	}
}
