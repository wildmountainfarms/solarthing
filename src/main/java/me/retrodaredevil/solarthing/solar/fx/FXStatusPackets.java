package me.retrodaredevil.solarthing.solar.fx;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;

import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;

import static me.retrodaredevil.util.json.JsonHelper.getOrNull;
import static me.retrodaredevil.solarthing.util.ParseUtil.toInt;

public final class FXStatusPackets {
	private FXStatusPackets(){ throw new UnsupportedOperationException(); }
	/**
	 *
	 * @param chars An array of chars with a length of 49. After this method is called, mutating this will have to effect on the created ImmutableFXStatusPacket
	 * @param ignoreCheckSum The IgnoreCheckSum enum value
	 * @return The created FXStatusPacket from chars
	 * @throws ParsePacketAsciiDecimalDigitException thrown if the formatting of the data in the chars array is incorrect
	 * @throws CheckSumException Thrown if the chksum from the chars array is not consistent with the other data. Never thrown if ignoreCheckSum != DISABLED
	 */
	public static FXStatusPacket createFromChars(char[] chars, IgnoreCheckSum ignoreCheckSum) throws ParsePacketAsciiDecimalDigitException, CheckSumException{
		if(chars.length != 49){
			throw new IllegalArgumentException("The passed chars array must have a length of 49! Got length of: " + chars.length + " with chars='" + new String(chars) + "'");
		}
		// start of status page
		final int address = toInt(chars, 1);
		// ,
		final int inverterCurrentTens = toInt(chars, 3);
		final int inverterCurrentOnes = toInt(chars, 4);
		// ,
		final int chargerCurrentTens = toInt(chars, 6);
		final int chargerCurrentOnes = toInt(chars, 7);
		// ,
		final int buyCurrentTens = toInt(chars, 9);
		final int buyCurrentOnes = toInt(chars, 10);
		// ,
		final int inputVoltageHundreds = toInt(chars, 12);
		final int inputVoltageTens = toInt(chars, 13);
		final int inputVoltageOnes = toInt(chars, 14);
		// ,
		final int outputVoltageHundreds = toInt(chars, 16);
		final int outputVoltageTens = toInt(chars, 17);
		final int outputVoltageOnes = toInt(chars, 18);
		// ,
		final int sellCurrentTens = toInt(chars, 20); // 00 to 99 - AC current the FX is delivering from batteries to AC input
		final int sellCurrentOnes = toInt(chars, 21);
		// ,
		final int operatingModeTens = toInt(chars, 23); // 00 to 99
		final int operatingModeOnes = toInt(chars, 24);
		// ,
		final int errorModeHundreds = toInt(chars, 26); // 000 to 255
		final int errorModeTens = toInt(chars, 27);
		final int errorModeOnes = toInt(chars, 28);
		// ,
		final int acModeTens = toInt(chars, 30); // 00 to 99 - status of AC input. 00:NO AC, 01:AC Drop, 02: AC Use
		final int acModeOnes = toInt(chars, 31);
		// ,
		final int batteryVoltageTens = toInt(chars, 33); // 000 to 999 -> XX.X
		final int batteryVoltageOnes = toInt(chars, 34);
		final int batteryVoltageTenths = toInt(chars, 35);
		// ,
		final int miscHundreds = toInt(chars, 37); // 000 to 255
		final int miscTens = toInt(chars, 38);
		final int miscOnes = toInt(chars, 39);
		// ,
		final int warningModeHundreds = toInt(chars, 41);
		final int warningModeTens = toInt(chars, 42);
		final int warningModeOnes = toInt(chars, 43);
		// ,
		final int chksumHundreds = toInt(chars, 45);
		final int chksumTens = toInt(chars, 46);
		final int chksumOnes = toInt(chars, 47);

		final int calculatedChksum = address + inverterCurrentTens + inverterCurrentOnes + chargerCurrentTens + chargerCurrentOnes +
				buyCurrentTens + buyCurrentOnes + inputVoltageHundreds + inputVoltageTens + inputVoltageOnes +
				outputVoltageHundreds + outputVoltageTens + outputVoltageOnes + sellCurrentTens + sellCurrentOnes +
				operatingModeTens + operatingModeOnes + errorModeHundreds + errorModeTens + errorModeOnes + acModeTens + acModeOnes +
				batteryVoltageTens + batteryVoltageOnes + batteryVoltageTenths + miscHundreds + miscTens + miscOnes + warningModeHundreds + warningModeTens + warningModeOnes;

		final int packetChksum = chksumHundreds * 100 + chksumTens * 10 + chksumOnes;

		if(packetChksum != calculatedChksum && ignoreCheckSum == IgnoreCheckSum.DISABLED){
			throw new CheckSumException(packetChksum, calculatedChksum, new String(chars));
		}
		final int chksum;
		switch(ignoreCheckSum){
			case DISABLED: case IGNORE:
				chksum = packetChksum;
				break;
			case IGNORE_AND_USE_CALCULATED:
				chksum = calculatedChksum;
				break;
			default:
				throw new IllegalArgumentException("Unknown IgnoreCheckSum enum value: " + ignoreCheckSum);
		}

		// set values
		final int inverterCurrentRaw = inverterCurrentTens * 10 + inverterCurrentOnes;
		final int chargerCurrentRaw = chargerCurrentTens * 10 + chargerCurrentOnes;
		final int buyCurrentRaw = buyCurrentTens * 10 + buyCurrentOnes;
		final int inputVoltageRaw = inputVoltageHundreds * 100 + inputVoltageTens * 10 + inputVoltageOnes;
		final int outputVoltageRaw = outputVoltageHundreds * 100 + outputVoltageTens * 10 + outputVoltageOnes;

		final int sellCurrentRaw = sellCurrentTens * 10 + sellCurrentOnes;
		final int operatingMode = operatingModeTens * 10 + operatingModeOnes;
		final int errorMode = errorModeHundreds * 100 + errorModeTens * 10 + errorModeOnes; // high middle and low
		final int acMode = acModeTens * 10 + acModeOnes; // high and low

		final float batteryVoltage = batteryVoltageTens * 10 + batteryVoltageOnes + (batteryVoltageTenths / 10.0f);
		final String batteryVoltageString = batteryVoltageTens + "" + batteryVoltageOnes + "." + batteryVoltageTenths;

		final int misc = miscHundreds * 100 + miscTens * 10 + miscOnes;
		final int warningMode = warningModeHundreds * 100 + warningModeTens * 10 + warningModeOnes;
		// chksum set above

		// Operational Mode Stuff ====
		final String operatingModeName = Modes.getActiveMode(OperationalMode.class, operatingMode, OperationalMode.UNKNOWN).getModeName();

		// ==== Error mode stuff ====
		final String errors = Modes.toString(FXErrorMode.class, errorMode);

		// ==== AC Mode Stuff ====
		final String acModeName = Modes.getActiveMode(ACMode.class, acMode, ACMode.UNKNOWN).getModeName();

		// ==== Misc Stuff ====
		final int inputVoltage, outputVoltage, inverterCurrent, chargerCurrent, buyCurrent, sellCurrent;
		if(MiscMode.FX_230V_UNIT.isActive(misc)){
			inputVoltage = inputVoltageRaw * 2;
			outputVoltage = inputVoltageRaw * 2;

			inverterCurrent = inverterCurrentRaw / 2;
			chargerCurrent = chargerCurrentRaw / 2;
			buyCurrent = buyCurrentRaw / 2;
			sellCurrent = sellCurrentRaw / 2;
		} else {
			inputVoltage = inputVoltageRaw;
			outputVoltage = outputVoltageRaw;

			inverterCurrent = inverterCurrentRaw;
			chargerCurrent = chargerCurrentRaw;
			buyCurrent = buyCurrentRaw;
			sellCurrent = sellCurrentRaw;
		}
		final String miscModes = Modes.toString(MiscMode.class, misc);

		// ==== Warning Mode stuff ====
		final String warnings = Modes.toString(WarningMode.class, warningMode);
		return new ImmutableFXStatusPacket(address, inverterCurrent, inverterCurrentRaw, chargerCurrent,
				chargerCurrentRaw, buyCurrent, buyCurrentRaw, inputVoltage, inputVoltageRaw, outputVoltage,
				outputVoltageRaw, sellCurrent, sellCurrentRaw, operatingMode, errorMode, acMode, batteryVoltage,
				batteryVoltageString, misc, warningMode, chksum, operatingModeName, errors, acModeName, miscModes, warnings);
	}

	public static FXStatusPacket createFromJson(JsonObject object){
		Objects.requireNonNull(object);
//		JsonFile.gson.fromJson
		final int address = object.get("address").getAsInt();

		final int inverterCurrent = object.get("inverterCurrent").getAsInt();
		final Integer storedInverterCurrentRaw = getOrNull(object, "inverterCurrentRaw", JsonElement::getAsInt);

		final int chargerCurrent = object.get("chargerCurrent").getAsInt();
		final Integer storedChargerCurrentRaw = getOrNull(object, "chargerCurrentRaw", JsonElement::getAsInt);

		final int buyCurrent = object.get("buyCurrent").getAsInt();
		final Integer storedBuyCurrentRaw = getOrNull(object, "buyCurrentRaw", JsonElement::getAsInt);

		final int sellCurrent = object.get("sellCurrent").getAsInt();
		final Integer storedSellCurrentRaw = getOrNull(object, "sellCurrentRaw", JsonElement::getAsInt);

		final int inputVoltage = object.get("inputVoltage").getAsInt();
		final Integer storedInputVoltageRaw = getOrNull(object, "inputVoltageRaw", JsonElement::getAsInt);

		final int outputVoltage = object.get("outputVoltage").getAsInt();
		final Integer storedOutputVoltageRaw = getOrNull(object, "outputVoltageRaw", JsonElement::getAsInt);

		final int operatingMode = object.get("operatingMode").getAsInt();
		final int errorMode = object.get("errorMode").getAsInt();
		final int acMode = object.get("acMode").getAsInt();

		final float batteryVoltage = object.get("batteryVoltage").getAsFloat();
		final String storedBatteryVoltageString = getOrNull(object, "batteryVoltageString", JsonElement::getAsString);
		final String batteryVoltageString = storedBatteryVoltageString != null ? storedBatteryVoltageString : Float.toString(batteryVoltage);

		final int misc = object.get("misc").getAsInt();
		final int warningMode = object.get("warningMode").getAsInt();
		final int chksum = object.get("chksum").getAsInt();

		final String storedOperatingModeName = getOrNull(object, "operatingModeName", JsonElement::getAsString);
		final String storedErrors = getOrNull(object, "errors", JsonElement::getAsString);
		final String storedAcModeName = getOrNull(object, "acModeName", JsonElement::getAsString);
		final String storedMiscModes = getOrNull(object, "miscModes", JsonElement::getAsString);
		final String storedWarnings = getOrNull(object, "warnings", JsonElement::getAsString);

		final int inverterCurrentRaw, chargerCurrentRaw, buyCurrentRaw, sellCurrentRaw, inputVoltageRaw, outputVoltageRaw;
		{
			final int number = MiscMode.FX_230V_UNIT.isActive(misc) ? 2 : 1;
			inputVoltageRaw = storedInputVoltageRaw != null ? storedInputVoltageRaw : inputVoltage / number;
			outputVoltageRaw = storedOutputVoltageRaw != null ? storedOutputVoltageRaw : outputVoltage / number;

			inverterCurrentRaw = storedInverterCurrentRaw != null ? storedInverterCurrentRaw : inverterCurrent * number;
			chargerCurrentRaw = storedChargerCurrentRaw != null ? storedChargerCurrentRaw : chargerCurrent * number;
			buyCurrentRaw = storedBuyCurrentRaw != null ? storedBuyCurrentRaw : buyCurrent * number;
			sellCurrentRaw = storedSellCurrentRaw != null ? storedSellCurrentRaw : sellCurrent * number;
		}

		final String operatingModeName, errors, acModeName, miscModes, warnings;
		{
			operatingModeName = storedOperatingModeName != null ? storedOperatingModeName : Modes.getActiveMode(OperationalMode.class, operatingMode, OperationalMode.UNKNOWN).getModeName();
			errors = storedErrors != null ? storedErrors : Modes.toString(FXErrorMode.class, errorMode);
			acModeName = storedAcModeName != null ? storedAcModeName : Modes.getActiveMode(ACMode.class, acMode, ACMode.UNKNOWN).getModeName();
			miscModes = storedMiscModes != null ? storedMiscModes : Modes.toString(MiscMode.class, misc);
			warnings = storedWarnings != null ? storedWarnings : Modes.toString(WarningMode.class, warningMode);
		}

		return new ImmutableFXStatusPacket(address, inverterCurrent, inverterCurrentRaw,
				chargerCurrent, chargerCurrentRaw, buyCurrent, buyCurrentRaw,
				inputVoltage, inputVoltageRaw, outputVoltage, outputVoltageRaw,
				sellCurrent, sellCurrentRaw, operatingMode, errorMode, acMode, batteryVoltage, batteryVoltageString,
				misc, warningMode, chksum, operatingModeName, errors, acModeName, miscModes, warnings);
	}
}
