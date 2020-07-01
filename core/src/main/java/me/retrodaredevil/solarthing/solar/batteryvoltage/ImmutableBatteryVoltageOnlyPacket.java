package me.retrodaredevil.solarthing.solar.batteryvoltage;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.misc.common.DataIdentifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class ImmutableBatteryVoltageOnlyPacket implements BatteryVoltageOnlyPacket {
	private final float batteryVoltage;
	private final int dataId;
	private final DataIdentifier identifier;
	private final IdentityInfo identityInfo;

	public ImmutableBatteryVoltageOnlyPacket(float batteryVoltage, int dataId) {
		this.batteryVoltage = batteryVoltage;
		this.dataId = dataId;

		identifier = new DataIdentifier(dataId);
		identityInfo = new BatteryVoltageOnlyIdentityInfo(dataId);
	}

	@Override
	public float getBatteryVoltage() {
		return batteryVoltage;
	}

	@Override
	public @NotNull DataIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public int getDataId() {
		return dataId;
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}
}
