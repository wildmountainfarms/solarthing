package me.retrodaredevil.solarthing.packet.fx;

import me.retrodaredevil.solarthing.packet.BitmaskMode;
import me.retrodaredevil.solarthing.packet.PacketType;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;

import static me.retrodaredevil.solarthing.util.ParseUtil.toInt;

public class ImmutableFXStatusPacket implements FXStatusPacket {
	private final PacketType packetType = PacketType.FX_STATUS;

	private final int address;

	private final int inverterCurrent, inverterCurrentRaw;
	private final int chargerCurrent, chargerCurrentRaw;
	private final int buyCurrent, buyCurrentRaw;
	private final int inputVoltage, inputVoltageRaw;
	private final int outputVoltage, outputVoltageRaw;
	private final int sellCurrent, sellCurrentRaw;
	private final int operatingMode, errorMode, acMode;

	private final float batteryVoltage;
	private final String batteryVoltageString;

	private final int misc, warningMode, chksum;

	private final String operatingModeName;
	private final String errors;
	private final String acModeName;
	private final String miscModes;
	private final String warnings;

	public ImmutableFXStatusPacket(int address,
								   int inverterCurrent, int inverterCurrentRaw,
								   int chargerCurrent, int chargerCurrentRaw,
								   int buyCurrent, int buyCurrentRaw,
								   int inputVoltage, int inputVoltageRaw,
								   int outputVoltage, int outputVoltageRaw,
								   int sellCurrent, int sellCurrentRaw,
								   int operatingMode, int errorMode, int acMode,
								   float batteryVoltage, String batteryVoltageString,
								   int misc, int warningMode, int chksum,
								   String operatingModeName, String errors, String acModeName, String miscModes, String warnings) {
		this.address = address;
		this.inverterCurrent = inverterCurrent;
		this.inverterCurrentRaw = inverterCurrentRaw;
		this.chargerCurrent = chargerCurrent;
		this.chargerCurrentRaw = chargerCurrentRaw;
		this.buyCurrent = buyCurrent;
		this.buyCurrentRaw = buyCurrentRaw;
		this.inputVoltage = inputVoltage;
		this.inputVoltageRaw = inputVoltageRaw;
		this.outputVoltage = outputVoltage;
		this.outputVoltageRaw = outputVoltageRaw;
		this.sellCurrent = sellCurrent;
		this.sellCurrentRaw = sellCurrentRaw;
		this.operatingMode = operatingMode;
		this.errorMode = errorMode;
		this.acMode = acMode;
		this.batteryVoltage = batteryVoltage;
		this.batteryVoltageString = batteryVoltageString;
		this.misc = misc;
		this.warningMode = warningMode;
		this.chksum = chksum;
		this.operatingModeName = operatingModeName;
		this.errors = errors;
		this.acModeName = acModeName;
		this.miscModes = miscModes;
		this.warnings = warnings;
	}
	public ImmutableFXStatusPacket(char[] chars) throws ParsePacketAsciiDecimalDigitException, CheckSumException{
		if(chars.length != 49){
			throw new IllegalArgumentException("The passed chars array must have a length of 49! Got length of: " + chars.length + " with chars='" + new String(chars) + "'");
		}
		// start of status page
		final int inverterAddressOnes = toInt(chars, 1);
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

		final int calculatedChksum = inverterAddressOnes + inverterCurrentTens + inverterCurrentOnes + chargerCurrentTens + chargerCurrentOnes +
				buyCurrentTens + buyCurrentOnes + inputVoltageHundreds + inputVoltageTens + inputVoltageOnes +
				outputVoltageHundreds + outputVoltageTens + outputVoltageOnes + sellCurrentTens + sellCurrentOnes +
				operatingModeTens + operatingModeOnes + errorModeHundreds + errorModeTens + errorModeOnes + acModeTens + acModeOnes +
				batteryVoltageTens + batteryVoltageOnes + batteryVoltageTenths + miscHundreds + miscTens + miscOnes + warningModeHundreds + warningModeTens + warningModeOnes;

		chksum = chksumHundreds * 100 + chksumTens * 10 + chksumOnes;

		if(chksum != calculatedChksum){
			throw new CheckSumException(chksum, calculatedChksum, new String(chars));
		}

		// set values
		address = inverterAddressOnes;
		inverterCurrentRaw = inverterCurrentTens * 10 + inverterCurrentOnes;
		chargerCurrentRaw = chargerCurrentTens * 10 + chargerCurrentOnes;
		buyCurrentRaw = buyCurrentTens * 10 + buyCurrentOnes;
		inputVoltageRaw = inputVoltageHundreds * 100 + inputVoltageTens * 10 + inputVoltageOnes;
		outputVoltageRaw = outputVoltageHundreds * 100 + outputVoltageTens * 10 + outputVoltageOnes;

		sellCurrentRaw = sellCurrentTens * 10 + sellCurrentOnes;
		operatingMode = operatingModeTens * 10 + operatingModeOnes;
		errorMode = errorModeHundreds * 100 + errorModeTens * 10 + errorModeOnes; // high middle and low
		acMode = acModeTens * 10 + acModeOnes;  // high and low

		batteryVoltage = batteryVoltageTens * 10 + batteryVoltageOnes + (batteryVoltageTenths / 10.0f);
		batteryVoltageString = Integer.toString(batteryVoltageTens) + Integer.toString(batteryVoltageOnes) + "." + Integer.toString(batteryVoltageTenths);

		misc = miscHundreds * 100 + miscTens * 10 + miscOnes;
		warningMode = warningModeHundreds * 100 + warningModeTens * 10 + warningModeOnes;
		// chksum set above

		// Operational Mode Stuff ====
		OperationalMode operationalMode = OperationalMode.getMode(operatingMode);
		operatingModeName = operationalMode.toString();

		// ==== Error mode stuff ====
		StringBuilder errorBuilder = new StringBuilder();
		for(FXErrorMode mode : FXErrorMode.values()){
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
		miscModes = BitmaskMode.toString(MiscMode.class, misc);

		// ==== Warning Mode stuff ====
		StringBuilder warningBuilder = new StringBuilder();
		for(WarningMode mode : WarningMode.values()){
			if(mode.isActive(warningMode)){
				warningBuilder.append(mode.toString()).append(", ");
			}
		}
		warnings = warningBuilder.toString();

	}


	@Override
	public int getInverterCurrent() {
        return inverterCurrent;
	}

	@Override
	public int getInverterCurrentRaw() {
        return inverterCurrentRaw;
	}

	@Override
	public int getChargerCurrent() {
        return chargerCurrent;
	}

	@Override
	public int getChargerCurrentRaw() {
        return chargerCurrentRaw;
	}

	@Override
	public int getBuyCurrent() {
        return buyCurrent;
	}

	@Override
	public int getBuyCurrentRaw() {
        return buyCurrentRaw;
	}

	@Override
	public int getInputVoltage() {
        return inputVoltage;
	}

	@Override
	public int getInputVoltageRaw() {
        return inputVoltageRaw;
	}

	@Override
	public int getOutputVoltage() {
        return outputVoltage;
	}

	@Override
	public int getOutputVoltageRaw() {
        return outputVoltageRaw;
	}

	@Override
	public int getSellCurrent() {
        return sellCurrent;
	}

	@Override
	public int getSellCurrentRaw() {
        return sellCurrentRaw;
	}

	@Override
	public int getOperatingMode() {
        return operatingMode;
	}

	@Override
	public int getErrorMode() {
        return errorMode;
	}

	@Override
	public int getACMode() {
        return acMode;
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
	public int getMisc() {
        return misc;
	}

	@Override
	public int getWarningMode() {
        return warningMode;
	}

	@Override
	public int getChksum() {
        return chksum;
	}

	@Override
	public String getOperatingModeName() {
        return operatingModeName;
	}

	@Override
	public String getErrorsString() {
        return errors;
	}

	@Override
	public String getACModeName() {
        return acModeName;
	}

	@Override
	public String getMiscModesString() {
        return miscModes;
	}

	@Override
	public String getWarningsString() {
        return warnings;
	}

	@Override
	public PacketType getPacketType() {
        return packetType;
	}

	@Override
	public int getAddress() {
        return address;
	}
}
