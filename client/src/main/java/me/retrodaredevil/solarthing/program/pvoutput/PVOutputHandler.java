package me.retrodaredevil.solarthing.program.pvoutput;

import me.retrodaredevil.solarthing.misc.weather.TemperaturePacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragmentMatcher;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParametersBuilder;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParametersBuilder;
import me.retrodaredevil.solarthing.solar.PowerUtil;
import me.retrodaredevil.solarthing.solar.common.PVCurrentAndVoltage;
import me.retrodaredevil.solarthing.solar.daily.DailyCalc;
import me.retrodaredevil.solarthing.solar.daily.DailyConfig;
import me.retrodaredevil.solarthing.solar.daily.DailyPair;
import me.retrodaredevil.solarthing.solar.daily.DailyUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.util.IdentifierUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PVOutputHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PVOutputHandler.class);

	private final TimeZone timeZone;
	private final Map<Integer, List<String>> requiredIdentifierMap;
	private final IdentifierFragmentMatcher voltageIdentifierFragmentMatcher;
	private final IdentifierFragmentMatcher temperatureIdentifierFragmentMatcher;

	public PVOutputHandler(TimeZone timeZone, Map<Integer, List<String>> requiredIdentifierMap, IdentifierFragmentMatcher voltageIdentifierFragmentMatcher, IdentifierFragmentMatcher temperatureIdentifierFragmentMatcher) {
		this.timeZone = timeZone;
		this.requiredIdentifierMap = requiredIdentifierMap;
		this.voltageIdentifierFragmentMatcher = voltageIdentifierFragmentMatcher;
		this.temperatureIdentifierFragmentMatcher = temperatureIdentifierFragmentMatcher;
	}
	public boolean checkPackets(long dayStartTimeMillis, List<FragmentedPacketGroup> packetGroupList) {
		if (packetGroupList.isEmpty()) {
			LOGGER.warn("No packets!");
			return false;
		}
		String reason = IdentifierUtil.getRequirementNotMetReason(requiredIdentifierMap, packetGroupList.get(packetGroupList.size() - 1));
		if (reason == null) {
			return true;
		}
		LOGGER.warn(reason);
		return false;
	}

	public AddStatusParameters getStatus(long dayStartTimeMillis, List<FragmentedPacketGroup> packetGroupList) {
		FragmentedPacketGroup latestPacketGroup = packetGroupList.get(packetGroupList.size() - 1);
		LOGGER.debug("Continuing with the latest packet group. Day start: " + dayStartTimeMillis);
		AddStatusParametersBuilder addStatusParametersBuilder = createStatusBuilder(timeZone, latestPacketGroup.getDateMillis());
		setStatusPowerValues(addStatusParametersBuilder, latestPacketGroup);
		setStatusEnergyValues(
				addStatusParametersBuilder,
				packetGroupList,
				DailyConfig.createDefault(dayStartTimeMillis)
		);
		for (Packet packet : latestPacketGroup.getPackets()) {
			if (packet instanceof PVCurrentAndVoltage) {
				int fragmentId = latestPacketGroup.getFragmentId(packet);
				PVCurrentAndVoltage pvCurrentAndVoltage = (PVCurrentAndVoltage) packet;
				IdentifierFragment identifierFragment = IdentifierFragment.create(fragmentId, pvCurrentAndVoltage.getIdentifier());
				if (voltageIdentifierFragmentMatcher.matches(identifierFragment)) {
					float voltage = pvCurrentAndVoltage.getPVVoltage().floatValue();
					addStatusParametersBuilder.setVoltage(voltage);
				}
			} else if (packet instanceof TemperaturePacket) {
				int fragmentId = latestPacketGroup.getFragmentId(packet);
				TemperaturePacket temperaturePacket = (TemperaturePacket) packet;
				IdentifierFragment identifierFragment = IdentifierFragment.create(fragmentId, temperaturePacket.getIdentifier());
				if (temperatureIdentifierFragmentMatcher.matches(identifierFragment)) {
					float temperatureCelsius = temperaturePacket.getTemperatureCelsius();
					if (!TemperaturePacket.POSSIBLE_BAD_VALUES.contains(temperatureCelsius)) {
						addStatusParametersBuilder.setTemperatureCelsius(temperatureCelsius);
					} else {
						System.out.println("Not setting temperature: " + temperatureCelsius + " because it could be a bad reading");
					}
				}
			}
		}
		return addStatusParametersBuilder.build();
	}
	private static AddStatusParametersBuilder createStatusBuilder(TimeZone timeZone, long dateMillis) {
		Calendar calendar = new GregorianCalendar(timeZone);
		calendar.setTimeInMillis(dateMillis);
		SimpleDate date = SimpleDate.fromCalendar(calendar);
		SimpleTime time = SimpleTime.fromCalendar(calendar);
		return new AddStatusParametersBuilder(date, time);
	}
	private static AddStatusParametersBuilder setStatusPowerValues(AddStatusParametersBuilder builder, PacketGroup latestPacketGroup){
		PowerUtil.Data data = PowerUtil.getPowerData(latestPacketGroup, PowerUtil.GeneratingType.PV_ONLY);
		return builder.setPowerGeneration(data.getGeneratingWatts())
				.setPowerConsumption(data.getConsumingWatts());
	}
	private static AddStatusParametersBuilder setStatusEnergyValues(AddStatusParametersBuilder builder, List<FragmentedPacketGroup> packetGroups, DailyConfig dailyConfig) {
		Map<IdentifierFragment, List<DailyPair<MXStatusPacket>>> mxMap = DailyUtil.getDailyPairs(DailyUtil.mapPackets(MXStatusPacket.class, packetGroups), dailyConfig);
		Map<IdentifierFragment, List<DailyPair<RoverStatusPacket>>> roverMap = DailyUtil.getDailyPairs(DailyUtil.mapPackets(RoverStatusPacket.class, packetGroups), dailyConfig);
		Map<IdentifierFragment, List<DailyPair<DailyFXPacket>>> dailyFXMap = DailyUtil.getDailyPairs(DailyUtil.mapPackets(DailyFXPacket.class, packetGroups), dailyConfig);
		if (!mxMap.isEmpty() || !roverMap.isEmpty()) { // energy produced
			float generationKWH = DailyCalc.getSumTotal(mxMap.values(), MXStatusPacket::getDailyKWH) +
					DailyCalc.getSumTotal(roverMap.values(), RoverStatusPacket::getDailyKWH);
			builder.setEnergyGeneration(Math.round(generationKWH * 1000.0f));
		}
		if (!dailyFXMap.isEmpty() || !roverMap.isEmpty()) { // energy consumed
			float consumptionKWH = DailyCalc.getSumTotal(roverMap.values(), RoverStatusPacket::getDailyKWHConsumption) +
					DailyCalc.getSumTotal(dailyFXMap.values(), dailyFXPacket -> dailyFXPacket.getInverterKWH() + dailyFXPacket.getBuyKWH() - dailyFXPacket.getChargerKWH());

			if (consumptionKWH >= 0) {// This if statement should almost always be executed, however, there are rare cases where (buy - charger) is negative for FX packets only
				builder.setEnergyConsumption(Math.round(consumptionKWH * 1000.0f));
			}
		}
		return builder;
	}
	public static AddOutputParametersBuilder setImportedExported(AddOutputParametersBuilder builder, List<FragmentedPacketGroup> packetGroups, DailyConfig dailyConfig, boolean includeImport, boolean includeExport) {
		Map<IdentifierFragment, List<DailyPair<DailyFXPacket>>> dailyFXMap = DailyUtil.getDailyPairs(DailyUtil.mapPackets(DailyFXPacket.class, packetGroups), dailyConfig);
		if (!dailyFXMap.isEmpty()) {
			if (includeImport) {
				float importKWH = DailyCalc.getSumTotal(dailyFXMap.values(), FXDailyData::getBuyKWH);
				builder.setImportOffPeak(Math.round(importKWH * 1000.0f));
			}
			if (includeExport) {
				float exportKWH = DailyCalc.getSumTotal(dailyFXMap.values(), FXDailyData::getSellKWH);
				builder.setExported(Math.round(exportKWH * 1000.0f));
			}
		}

		return builder;
	}

}
