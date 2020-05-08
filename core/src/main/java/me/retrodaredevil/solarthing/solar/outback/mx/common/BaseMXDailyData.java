package me.retrodaredevil.solarthing.solar.outback.mx.common;

import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonExplicit
public abstract class BaseMXDailyData extends ImmutableMXDailyData implements SupplementaryIdentifiable {
	private final KnownSupplementaryIdentifier<OutbackIdentifier> supplementaryIdentifier;

	public BaseMXDailyData(
			DocumentedPacketType packetType,
			MXDailyData dailyData,
			OutbackIdentifier outbackIdentifier
	) {
		super(dailyData.getAddress(), dailyData.getErrorModeValue(), dailyData.getStartDateMillis(), dailyData.getDailyKWH(), dailyData.getDailyAH(), dailyData.getDailyAHSupport(), dailyData.getDailyMinBatteryVoltage(), dailyData.getDailyMaxBatteryVoltage());
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
