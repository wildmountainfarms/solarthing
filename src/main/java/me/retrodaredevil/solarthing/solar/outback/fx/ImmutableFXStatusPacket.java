package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.SolarPacketType;

@SuppressWarnings("unused")
final class ImmutableFXStatusPacket implements FXStatusPacket {
	private final SolarPacketType packetType = SolarPacketType.FX_STATUS;

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
	
	private final transient Identifier identifier;

	ImmutableFXStatusPacket(
			int address,
			int inverterCurrent, int inverterCurrentRaw,
			int chargerCurrent, int chargerCurrentRaw,
			int buyCurrent, int buyCurrentRaw,
			int inputVoltage, int inputVoltageRaw,
			int outputVoltage, int outputVoltageRaw,
			int sellCurrent, int sellCurrentRaw,
			int operatingMode, int errorMode, int acMode,
			float batteryVoltage, String batteryVoltageString,
			int misc, int warningMode, int chksum,
			String operatingModeName, String errors, String acModeName, String miscModes, String warnings
	) {
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
		
		this.identifier = new OutbackIdentifier(address);
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
	public int getOperatingModeValue() {
		return operatingMode;
	}

	@Override
	public int getErrorMode() {
		return errorMode;
	}

	@Override
	public int getACModeValue() {
		return acMode;
	}

	@Override
	public float getBatteryVoltage() {
		return batteryVoltage;
	}
	
	/**
	 * Should be serialized as "batteryVoltageString" if serialized at all
	 * @return The battery voltage as a String
	 */
	@Deprecated
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
	public SolarPacketType getPacketType() {
		return packetType;
	}

	@Override
	public int getAddress() {
		return address;
	}
	
	@Override
	public Identifier getIdentifier() {
		return identifier;
	}
}
