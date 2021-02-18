package me.retrodaredevil.solarthing.solar.pzem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.misc.common.DataIdentifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class ImmutablePzemShuntStatusPacket implements PzemShuntStatusPacket {
	private final DataIdentifier identifier;
	private final IdentityInfo identityInfo;
	private final int voltageValueRaw, currentValueRaw, powerValueRaw, energyValueRaw;
	private final int highVoltageAlarmStatus, lowVoltageAlarmStatus;
	private final int modbusAddress;
	@JsonCreator
	public ImmutablePzemShuntStatusPacket(
			@JsonProperty("dataId") int dataId,
			@JsonProperty("voltageValueRaw") int voltageValueRaw, @JsonProperty("currentValueRaw") int currentValueRaw,
			@JsonProperty("powerValueRaw") int powerValueRaw, @JsonProperty("energyValueRaw") int energyValueRaw,
			@JsonProperty("highVoltageAlarmStatus") int highVoltageAlarmStatus, @JsonProperty("lowVoltageAlarmStatus") int lowVoltageAlarmStatus,
			@JsonProperty("modbusAddress") int modbusAddress) {
		this.identifier = new DataIdentifier(dataId);
		this.identityInfo = new PzemShuntIdentityInfo(dataId);

		this.voltageValueRaw = voltageValueRaw;
		this.currentValueRaw = currentValueRaw;
		this.powerValueRaw = powerValueRaw;
		this.energyValueRaw = energyValueRaw;
		this.highVoltageAlarmStatus = highVoltageAlarmStatus;
		this.lowVoltageAlarmStatus = lowVoltageAlarmStatus;
		this.modbusAddress = modbusAddress;
	}
	public static ImmutablePzemShuntStatusPacket createFromReadTable(int dataId, int modbusAddress, PzemShuntReadTable read) {
		return new ImmutablePzemShuntStatusPacket(
				dataId,
				read.getVoltageValueRaw(), read.getCurrentValueRaw(), read.getPowerValueRaw(), read.getEnergyValueRaw(),
				read.getHighVoltageAlarmStatus(), read.getLowVoltageAlarmStatus(),
				modbusAddress
		);
	}
	@Override
	public @NotNull DataIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public int getDataId() {
		return identifier.getDataId();
	}

	@Override
	public int getVoltageValueRaw() {
		return voltageValueRaw;
	}

	@Override
	public int getCurrentValueRaw() {
		return currentValueRaw;
	}

	@Override
	public int getPowerValueRaw() {
		return powerValueRaw;
	}

	@Override
	public int getEnergyValueRaw() {
		return energyValueRaw;
	}

	@Override
	public int getHighVoltageAlarmStatus() {
		return highVoltageAlarmStatus;
	}

	@Override
	public int getLowVoltageAlarmStatus() {
		return lowVoltageAlarmStatus;
	}

	@Override
	public int getModbusAddress() {
		return modbusAddress;
	}
}
