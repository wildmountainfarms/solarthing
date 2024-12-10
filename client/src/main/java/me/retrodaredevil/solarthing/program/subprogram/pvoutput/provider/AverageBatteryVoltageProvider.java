package me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;

import java.util.DoubleSummaryStatistics;

@JsonTypeName("average-battery-voltage")
public enum AverageBatteryVoltageProvider implements VoltageProvider {
	INSTANCE;

	@JsonCreator
	private static AverageBatteryVoltageProvider getInstance() {
		return INSTANCE;
	}

	@Override
	public @Nullable Result getResult(FragmentedPacketGroup fragmentedPacketGroup) {
		DoubleSummaryStatistics statistics = fragmentedPacketGroup.getPackets().stream()
				.filter(packet -> packet instanceof BatteryVoltage)
				.map(packet -> (BatteryVoltage) packet)
				.mapToDouble(BatteryVoltage::getBatteryVoltage)
				.summaryStatistics();
		if (statistics.getCount() == 0) {
			return null;
		}
		return Result.ofVoltage((float) statistics.getAverage());
	}
}
