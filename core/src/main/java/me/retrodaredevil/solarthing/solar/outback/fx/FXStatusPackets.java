package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;

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
	@SuppressWarnings("Duplicates")
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

		final int misc = miscHundreds * 100 + miscTens * 10 + miscOnes;
		final int warningMode = warningModeHundreds * 100 + warningModeTens * 10 + warningModeOnes;
		// chksum set above

		// ==== Misc Stuff ====
		final int inputVoltage, outputVoltage;
		final float inverterCurrent, chargerCurrent, buyCurrent, sellCurrent;
		if(MiscMode.FX_230V_UNIT.isActive(misc)){
			inputVoltage = inputVoltageRaw * 2;
			outputVoltage = inputVoltageRaw * 2;

			inverterCurrent = inverterCurrentRaw / 2f;
			chargerCurrent = chargerCurrentRaw / 2f;
			buyCurrent = buyCurrentRaw / 2f;
			sellCurrent = sellCurrentRaw / 2f;
		} else {
			inputVoltage = inputVoltageRaw;
			outputVoltage = outputVoltageRaw;

			inverterCurrent = inverterCurrentRaw;
			chargerCurrent = chargerCurrentRaw;
			buyCurrent = buyCurrentRaw;
			sellCurrent = sellCurrentRaw;
		}

		return new ImmutableFXStatusPacket(
				FXStatusPacket.VERSION_NO_MORE_CONVENIENCE_FIELDS, address, inverterCurrent, inverterCurrentRaw, chargerCurrent,
				chargerCurrentRaw, buyCurrent, buyCurrentRaw, inputVoltage, inputVoltageRaw, outputVoltage,
				outputVoltageRaw, sellCurrent, sellCurrentRaw, operatingMode, errorMode, acMode, batteryVoltage,
				misc, warningMode, chksum
		);
	}
}
