package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

import java.beans.ConstructorProperties;

final class ImmutableMXStatusPacket implements MXStatusPacket {
	private final int address;
	private final int chargerCurrent, pvCurrent, inputVoltage;
	private final float dailyKWH;
	private final float ampChargerCurrent;
	private final int auxMode, errorMode, chargerMode;

	private final float batteryVoltage;

	private final int dailyAH;
	private final Support dailyAHSupport;
	private final int chksum;

	private final OutbackIdentifier identifier;

	/*
	The structure of MXStatusPackets has not changed much, so we don't need a complex way to deserialize them.
	 */
	@ConstructorProperties({
			"address", "chargerCurrent", "pvCurrent", "inputVoltage", "dailyKWH", "ampChargerCurrent",
			"auxMode", "errorMode", "chargerMode", "batteryVoltage", "dailyAH", "dailyAHSupport", "chksum"
	})
	ImmutableMXStatusPacket(
			int address, int chargerCurrent, int pvCurrent, int inputVoltage,
			float dailyKWH,
			float ampChargerCurrent,
			int auxMode, int errorMode, int chargerMode,
			float batteryVoltage,
			int dailyAH, Support dailyAHSupport,
			int chksum
	) {
		this.address = address;
		this.chargerCurrent = chargerCurrent;
		this.pvCurrent = pvCurrent;
		this.inputVoltage = inputVoltage;
		this.dailyKWH = dailyKWH;
		this.ampChargerCurrent = ampChargerCurrent;
		this.auxMode = auxMode;
		this.errorMode = errorMode;
		this.chargerMode = chargerMode;
		this.batteryVoltage = batteryVoltage;
		this.dailyAH = dailyAH;
		this.dailyAHSupport = dailyAHSupport == null ? Support.UNKNOWN : dailyAHSupport;
		this.chksum = chksum;

		this.identifier = new OutbackIdentifier(address);
	}

	@Override
	public SolarStatusPacketType getPacketType() {
		return SolarStatusPacketType.MXFM_STATUS;
	}

	@Override
	public int getAddress() {
		return address;
	}
	
	@Override
	public OutbackIdentifier getIdentifier() {
		return identifier;
	}

	@Deprecated
	@Override
	public int getChargerCurrent() {
		return chargerCurrent;
	}

	@Override
	public Integer getPVCurrent() {
		return pvCurrent;
	}

	@Override
	public Integer getInputVoltage() {
		return inputVoltage;
	}

	@Override
	public float getDailyKWH() {
		return dailyKWH;
	}


	@Deprecated
	@Override
	public float getAmpChargerCurrent() {
		return ampChargerCurrent;
	}


	@Override
	public int getRawAuxModeValue() {
		return auxMode;
	}

	@Override
	public int getErrorModeValue() {
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
	public int getDailyAH() {
		return dailyAH;
	}
	
	@Override
	public Support getDailyAHSupport() {
		return dailyAHSupport;
	}
	
	@Override
	public int getChksum() {
		return chksum;
	}
}
