package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.solar.SolarPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

@SuppressWarnings("unused")
final class ImmutableFXStatusPacket implements FXStatusPacket {
	private final SolarPacketType packetType = SolarPacketType.FX_STATUS;

	private final int address;

	private final float inverterCurrent;
	private final int inverterCurrentRaw;
	private final float chargerCurrent;
	private final int chargerCurrentRaw;
	private final float buyCurrent;
	private final int buyCurrentRaw;
	private final int inputVoltage, inputVoltageRaw;
	private final int outputVoltage, outputVoltageRaw;
	private final float sellCurrent;
	private final int sellCurrentRaw;
	private final int operatingMode, errorMode, acMode;

	private final float batteryVoltage;

	private final int misc, warningMode, chksum;

	private final String operatingModeName;
	private final String errors;
	private final String acModeName;
	private final String miscModes;
	private final String warnings;
	
	private final transient OutbackIdentifier identifier;

	ImmutableFXStatusPacket(
			int address,
			float inverterCurrent, int inverterCurrentRaw,
			float chargerCurrent, int chargerCurrentRaw,
			float buyCurrent, int buyCurrentRaw,
			int inputVoltage, int inputVoltageRaw,
			int outputVoltage, int outputVoltageRaw,
			float sellCurrent, int sellCurrentRaw,
			int operatingMode, int errorMode, int acMode,
			float batteryVoltage,
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
	public float getInverterCurrent() {
		return inverterCurrent;
	}

	@Override
	public int getInverterCurrentRaw() {
		return inverterCurrentRaw;
	}

	@Override
	public float getChargerCurrent() {
		return chargerCurrent;
	}

	@Override
	public int getChargerCurrentRaw() {
		return chargerCurrentRaw;
	}

	@Override
	public float getBuyCurrent() {
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
	public float getSellCurrent() {
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
	public OutbackIdentifier getIdentifier() {
		return identifier;
	}
}
