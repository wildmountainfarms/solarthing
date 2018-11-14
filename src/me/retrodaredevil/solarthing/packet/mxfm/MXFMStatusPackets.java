package me.retrodaredevil.solarthing.packet.mxfm;

import me.retrodaredevil.solarthing.packet.BitmaskMode;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;

import static me.retrodaredevil.solarthing.util.ParseUtil.toInt;

public final class MXFMStatusPackets {
	private MXFMStatusPackets(){ throw new UnsupportedOperationException(); }

	public static ImmutableMXFMStatusPacket createFromChars(char[] chars, IgnoreCheckSum ignoreCheckSum) throws CheckSumException, ParsePacketAsciiDecimalDigitException {
		// Start of Status Page
		final int address = ((int) chars[1]) - 65; // if it is "A" then address would be 0
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

		final int packetChksum = chksumHundreds * 100 + chksumTens * 10 + chksumOnes;

		if(packetChksum != calculatedChksum && ignoreCheckSum == IgnoreCheckSum.DISABLED){
			throw new CheckSumException(packetChksum, calculatedChksum, new String(chars));
		}
		final int chksum;
		switch (ignoreCheckSum){
			case DISABLED: case IGNORE:
				chksum = packetChksum;
				break;
			case IGNORE_AND_USE_CALCULATED:
				chksum = calculatedChksum;
				break;
			default:
				throw new RuntimeException("Unknown IgnoreCheckSum enum value: " + ignoreCheckSum);
		}


		final int chargerCurrent = chargerCurrentTens * 10 + chargerCurrentOnes;
		final int pvCurrent = pvCurrentTens * 10 + pvCurrentOnes;
		final int inputVoltage = inputVoltageHundreds * 100 + inputVoltageTens * 10 + inputVoltageOnes;

		final float dailyKWH = dailyKWHTens * 10 + dailyKWHOnes + (dailyKWHTenths / 10.0f);
		final String dailyKWHString = dailyKWHTens + "" + dailyKWHOnes + "." + dailyKWHTenths;
		final float ampChargerCurrent = ampChargerCurrentTenths / 10.0f;
		final String ampChargerCurrentString = "0." + ampChargerCurrentTenths;

		final int auxMode = auxModeTens * 10 + auxModeOnes;
		final int errorMode = errorModeHundreds * 100 + errorModeTens * 10 + errorModeOnes;
		final int chargerMode = chargerModeTens * 10 + chargerModeOnes;

		final float batteryVoltage = batteryVoltageTens * 10 + batteryVoltageOnes + (batteryVoltageTenths / 10.0f);
		final String batteryVoltageString = batteryVoltageTens + "" + batteryVoltageOnes + "." + batteryVoltageTenths;

		final int dailyAH = dailyAHThousands * 1000 + dailyAHHundreds * 100 + dailyAHTens * 10 + dailyAHOnes; // will be 9999 if MX60

		// ==== Aux Mode stuff ====
		final String auxModeName = AuxMode.getAuxMode(auxMode).toString();

		// ==== Error Mode stuff ====
		final String errors = BitmaskMode.toString(MXFMErrorMode.class, errorMode);

		// ==== Charge Mode stuff ====
		final String chargerModeName = ChargerMode.getMode(chargerMode).toString();
		return new ImmutableMXFMStatusPacket(address, chargerCurrent, pvCurrent, inputVoltage, dailyKWH,
				dailyKWHString, ampChargerCurrent, ampChargerCurrentString, auxMode, errorMode, chargerMode,
				batteryVoltage, batteryVoltageString, dailyAH, chksum, auxModeName, errors, chargerModeName);
	}
}
