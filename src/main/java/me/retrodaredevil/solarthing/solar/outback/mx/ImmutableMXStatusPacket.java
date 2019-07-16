package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.SolarPacketType;

@SuppressWarnings("unused")
class ImmutableMXStatusPacket implements MXStatusPacket {
	private final SolarPacketType packetType = SolarPacketType.MXFM_STATUS;

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
	
	private final transient Identifier identifier;

	ImmutableMXStatusPacket(
			int address, int chargerCurrent, int pvCurrent, int inputVoltage,
			float dailyKWH, String dailyKWHString,
			float ampChargerCurrent, String ampChargerCurrentString,
			int auxMode, int errorMode, int chargerMode,
			float batteryVoltage, String batteryVoltageString,
			int dailyAH, int chksum, String auxModeName, String errors, String chargerModeName
	) {
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
