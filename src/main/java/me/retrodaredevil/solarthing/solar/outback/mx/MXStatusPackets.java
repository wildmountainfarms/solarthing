package me.retrodaredevil.solarthing.solar.outback.mx;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;

import static me.retrodaredevil.util.json.JsonHelper.getOrNull;
import static me.retrodaredevil.solarthing.util.ParseUtil.toInt;

public final class MXStatusPackets {
	private MXStatusPackets(){ throw new UnsupportedOperationException(); }

	@SuppressWarnings("Duplicates")
	public static ImmutableMXStatusPacket createFromChars(char[] chars, IgnoreCheckSum ignoreCheckSum) throws CheckSumException, ParsePacketAsciiDecimalDigitException {
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
		final boolean oldFirmware = !Character.isDigit(char41); // if the dailyAH spot doesn't use all 4 digits, it must be on old firmware
		if(!oldFirmware){ // then this must use all four places
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
		final Support dailyAHSupported;
		if(oldFirmware || dailyAH == 9999){
			if(dailyAH != 0 && dailyAH != 9999){
				dailyAHSupported = Support.UNKNOWN;
			} else {
				dailyAHSupported = Support.NOT_SUPPORTED;
			}
		} else {
			dailyAHSupported = Support.FULLY_SUPPORTED;
		}

		// ==== Aux Mode stuff ====
		final String auxModeName = Modes.getActiveMode(AuxMode.class, auxMode).getModeName();

		// ==== Error Mode stuff ====
		final String errors = Modes.toString(MXErrorMode.class, errorMode);

		// ==== Charge Mode stuff ====
		final String chargerModeName = Modes.getActiveMode(ChargerMode.class, chargerMode).getModeName();
		return new ImmutableMXStatusPacket(address, chargerCurrent, pvCurrent, inputVoltage, dailyKWH,
				dailyKWHString, ampChargerCurrent, ampChargerCurrentString, auxMode, errorMode, chargerMode,
				batteryVoltage, batteryVoltageString, dailyAH, dailyAHSupported, chksum, auxModeName, errors, chargerModeName);
	}

	public static MXStatusPacket createFromJson(JsonObject object) {

		final int address = object.get("address").getAsInt();

		final int chargerCurrent = object.get("chargerCurrent").getAsInt();
		final int pvCurrent = object.get("pvCurrent").getAsInt();
		final int inputVoltage = object.get("inputVoltage").getAsInt();

		final float dailyKWH = object.get("dailyKWH").getAsFloat();
		final String storedDailyKWHString = getOrNull(object, "dailyKWHString", JsonElement::getAsString);

		final float ampChargerCurrent = object.get("ampChargerCurrent").getAsFloat();
		final String storedAmpChargerCurrentString = getOrNull(object, "ampChargerCurrentString", JsonElement::getAsString);

		final int auxMode = object.get("auxMode").getAsInt();
		final int errorMode = object.get("errorMode").getAsInt();
		final int chargerMode = object.get("chargerMode").getAsInt();

		final float batteryVoltage = object.get("batteryVoltage").getAsFloat();
		final String storedBatteryVoltageString = getOrNull(object, "batteryVoltageString", JsonElement::getAsString);

		final String dailyKWHString = storedDailyKWHString != null ? storedDailyKWHString : Float.toString(dailyKWH);
		final String ampChargerCurrentString = storedAmpChargerCurrentString != null ? storedAmpChargerCurrentString : Float.toString(ampChargerCurrent);
		final String batteryVoltageString = storedBatteryVoltageString != null ? storedBatteryVoltageString : Float.toString(batteryVoltage);

		final int dailyAH = object.get("dailyAH").getAsInt();
		final String dailyAHSupportString = getOrNull(object, "dailyAHSupport", JsonElement::getAsString);
		final Support dailyAHSupport;
		if(dailyAHSupportString == null){
			dailyAHSupport = Support.UNKNOWN;
		} else {
			dailyAHSupport = Support.valueOf(dailyAHSupportString);
		}
		final int chksum = object.get("chksum").getAsInt();

		final String storedAuxModeName = getOrNull(object, "auxModeName", JsonElement::getAsString);
		final String storedErrors = getOrNull(object, "error", JsonElement::getAsString);
		final String storedChargerModeName = getOrNull(object, "chargerModeName", JsonElement::getAsString);

		final String auxModeName = storedAuxModeName != null ? storedAuxModeName : Modes.getActiveMode(AuxMode.class, auxMode).getModeName();
		final String errors = storedErrors != null ? storedErrors : Modes.toString(MXErrorMode.class, errorMode);
		final String chargerModeName = storedChargerModeName != null ? storedChargerModeName : Modes.getActiveMode(ChargerMode.class, chargerMode).getModeName();


		return new ImmutableMXStatusPacket(address, chargerCurrent, pvCurrent, inputVoltage, dailyKWH, dailyKWHString,
				ampChargerCurrent, ampChargerCurrentString, auxMode, errorMode, chargerMode, batteryVoltage, batteryVoltageString,
				dailyAH, dailyAHSupport, chksum, auxModeName, errors, chargerModeName);
	}
}
