package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

@SuppressWarnings("unused")
final class ImmutableMXStatusPacket implements MXStatusPacket {
	private final SolarStatusPacketType packetType = SolarStatusPacketType.MXFM_STATUS;

	private final int address;
	private final int chargerCurrent, pvCurrent, inputVoltage;
	private final float dailyKWH;
	private final float ampChargerCurrent;
	private final int auxMode, errorMode, chargerMode;

	private final float batteryVoltage;

	private final int dailyAH;
	private final Support dailyAHSupport;
	private final int chksum;

	private final String auxModeName;
	private final String errors;
	private final String chargerModeName;
	
	private final transient OutbackIdentifier identifier;
	
	ImmutableMXStatusPacket(
		int address, int chargerCurrent, int pvCurrent, int inputVoltage,
		float dailyKWH,
		float ampChargerCurrent,
		int auxMode, int errorMode, int chargerMode,
		float batteryVoltage,
		int dailyAH, Support dailyAHSupport,
		int chksum, String auxModeName, String errors, String chargerModeName
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
		this.dailyAHSupport = dailyAHSupport;
		this.chksum = chksum;
		this.auxModeName = auxModeName;
		this.errors = errors;
		this.chargerModeName = chargerModeName;
		
		this.identifier = new OutbackIdentifier(address);
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
	public SolarStatusPacketType getPacketType() {
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

	@SuppressWarnings("deprecation")
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


	@SuppressWarnings("deprecation")
	@Override
	public float getAmpChargerCurrent() {
		return ampChargerCurrent;
	}


	@Override
	public int getRawAuxModeValue() {
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
