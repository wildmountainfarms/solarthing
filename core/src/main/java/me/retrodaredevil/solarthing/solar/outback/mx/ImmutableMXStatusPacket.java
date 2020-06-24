package me.retrodaredevil.solarthing.solar.outback.mx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonIgnoreProperties(value = {
		"auxModeName", "errors", "chargerModeName",
		"batteryVoltageString", "dailyKWHString", "ampChargerCurrentString"
}, allowGetters = true)
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
	private final IdentityInfo identityInfo;

	@JsonCreator
	ImmutableMXStatusPacket(
			@JsonProperty(value = "address", required = true) int address, @JsonProperty(value = "chargerCurrent", required = true) int chargerCurrent, @JsonProperty(value = "pvCurrent", required = true) int pvCurrent, @JsonProperty(value = "inputVoltage", required = true) int inputVoltage,
			@JsonProperty(value = "dailyKWH", required = true) float dailyKWH,
			@JsonProperty(value = "ampChargerCurrent", required = true) float ampChargerCurrent,
			@JsonProperty(value = "auxMode", required = true) int auxMode, @JsonProperty(value = "errorMode", required = true) int errorMode, @JsonProperty(value = "chargerMode", required = true) int chargerMode,
			@JsonProperty(value = "batteryVoltage", required = true) float batteryVoltage,
			@JsonProperty(value = "dailyAH", required = true) int dailyAH, @JsonProperty(value = "dailyAHSupport") Support dailyAHSupport,
			@JsonProperty(value = "chksum", required = true) int chksum
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
		this.identityInfo = new MXIdentityInfo(address);
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public int getAddress() {
		return address;
	}

	@Override
	public @NotNull OutbackIdentifier getIdentifier() {
		return identifier;
	}

	@Deprecated
	@Override
	public int getChargerCurrent() {
		return chargerCurrent;
	}

	@NotNull
	@Override
	public Integer getPVCurrent() {
		return pvCurrent;
	}

	@NotNull
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
	public int getChargerModeValue() {
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

	@NotNull
	@Override
	public Support getDailyAHSupport() {
		return dailyAHSupport;
	}

	@Override
	public int getChksum() {
		return chksum;
	}
}
