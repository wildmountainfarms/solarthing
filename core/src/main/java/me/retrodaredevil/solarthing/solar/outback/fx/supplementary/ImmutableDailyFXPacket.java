package me.retrodaredevil.solarthing.solar.outback.fx.supplementary;

import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.supplementary.SupplementarySolarPacketType;

import java.util.Collection;

class ImmutableDailyFXPacket implements DailyFXPacket {
	private static final String SUPPLEMENTARY_TYPE = SupplementarySolarPacketType.FX_DAILY.toString();
	private final SupplementarySolarPacketType packetType = SupplementarySolarPacketType.FX_DAILY;

	private final float dailyMinBatteryVoltage;
	private final float dailyMaxBatteryVoltage;

	private final float inverterKWH;
	private final float chargerKWH;
	private final float buyKWH;
	private final float sellKWH;

	private final Collection<Integer> operationalModeValues;
	private final int errorModeValue;
	private final int warningModeValue;
	private final int miscValue;
	private final Collection<Integer> acModeValues;

	private final int address;

	private final transient SupplementaryIdentifier supplementaryIdentifier;

	public ImmutableDailyFXPacket(
			float dailyMinBatteryVoltage, float dailyMaxBatteryVoltage, float inverterKWH, float chargerKWH, float buyKWH, float sellKWH,
			Collection<Integer> operationalModeValues, int errorModeValue, int warningModeValue, int miscValue, Collection<Integer> acModeValues,
			OutbackIdentifier outbackIdentifier
	) {
		this.dailyMinBatteryVoltage = dailyMinBatteryVoltage;
		this.dailyMaxBatteryVoltage = dailyMaxBatteryVoltage;
		this.inverterKWH = inverterKWH;
		this.chargerKWH = chargerKWH;
		this.buyKWH = buyKWH;
		this.sellKWH = sellKWH;
		this.operationalModeValues = operationalModeValues;
		this.errorModeValue = errorModeValue;
		this.warningModeValue = warningModeValue;
		this.miscValue = miscValue;
		this.acModeValues = acModeValues;

		this.address = outbackIdentifier.getAddress();
		this.supplementaryIdentifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, SUPPLEMENTARY_TYPE);
	}

	@Override
	public SupplementaryIdentifier getIdentifier() {
		return supplementaryIdentifier;
	}

	@Override
	public SupplementarySolarPacketType getPacketType() {
		return packetType;
	}

	@Override public float getDailyMinBatteryVoltage() { return dailyMinBatteryVoltage; }
	@Override public float getDailyMaxBatteryVoltage() { return dailyMaxBatteryVoltage; }

	@Override public float getInverterKWH() { return inverterKWH; }
	@Override public float getChargerKWH() { return chargerKWH; }
	@Override public float getBuyKWH() { return buyKWH; }
	@Override public float getSellKWH() { return sellKWH; }

	@Override public Collection<Integer> getOperationalModeValues() { return operationalModeValues; }
	@Override public int getErrorModeValue() { return errorModeValue; }
	@Override public int getWarningModeValue() { return warningModeValue; }
	@Override public int getMiscValue() { return miscValue; }
	@Override public Collection<Integer> getACModeValues() { return acModeValues; }

}
