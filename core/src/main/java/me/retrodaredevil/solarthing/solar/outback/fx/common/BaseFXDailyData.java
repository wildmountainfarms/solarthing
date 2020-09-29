package me.retrodaredevil.solarthing.solar.outback.fx.common;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

public abstract class BaseFXDailyData extends ImmutableFXDailyData implements SupplementaryIdentifiable {

	private final KnownSupplementaryIdentifier<OutbackIdentifier> supplementaryIdentifier;

	@SuppressWarnings("deprecation")
	public BaseFXDailyData(
			DocumentedPacketType packetType,
			FXDailyData dailyData,
			OutbackIdentifier outbackIdentifier
	) {
		super(
				dailyData.getAddress(), dailyData.getStartDateMillis(), dailyData.getDailyMinBatteryVoltage(), dailyData.getDailyMaxBatteryVoltage(),
				dailyData.getInverterKWH(), dailyData.getChargerKWH(), dailyData.getBuyKWH(), dailyData.getSellKWH(), dailyData.getOperationalModeValues(),
				dailyData.getErrorModeValue(), dailyData.getWarningModeValue(), dailyData.getMiscValue(), dailyData.getACModeValues()
		);
		int address = dailyData.getAddress();
		int identifierAddress = outbackIdentifier.getAddress();
		if(address != identifierAddress){
			throw new IllegalArgumentException("The address from dailyData and from outbackIdentifier are different! dailyData.getAddress()=" + address + " and outbackIdentifier.getAddress()=" + identifierAddress);
		}
		supplementaryIdentifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, packetType.toString());
	}

	@Override
	public @NotNull KnownSupplementaryIdentifier<OutbackIdentifier> getIdentifier() {
		return supplementaryIdentifier;
	}

}
