package me.retrodaredevil.solarthing.program.pvoutput;

import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.program.pvoutput.provider.DataProvider;
import me.retrodaredevil.solarthing.program.pvoutput.provider.TemperatureCelsiusProvider;
import me.retrodaredevil.solarthing.program.pvoutput.provider.VoltageProvider;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParametersBuilder;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParametersBuilder;
import me.retrodaredevil.solarthing.solar.PowerUtil;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationCalc;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationConfig;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationPair;
import me.retrodaredevil.solarthing.solar.accumulation.AccumulationUtil;
import me.retrodaredevil.solarthing.solar.accumulation.value.FloatAccumulationValue;
import me.retrodaredevil.solarthing.solar.accumulation.value.FloatAccumulationValueFactory;
import me.retrodaredevil.solarthing.solar.common.DailyAdvancedChargeController;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.util.IdentifierUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class PVOutputHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PVOutputHandler.class);

	private final ZoneId zoneId;
	private final Map<Integer, List<String>> requiredIdentifierMap;
	private final VoltageProvider voltageProvider;
	private final TemperatureCelsiusProvider temperatureCelsiusProvider;


	public PVOutputHandler(ZoneId zoneId, Map<Integer, List<String>> requiredIdentifierMap, VoltageProvider voltageProvider, TemperatureCelsiusProvider temperatureCelsiusProvider) {
		this.zoneId = zoneId;
		this.requiredIdentifierMap = requiredIdentifierMap;
		this.voltageProvider = voltageProvider;
		this.temperatureCelsiusProvider = temperatureCelsiusProvider;
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
		/*
		TODO think about if we really want to use the grouped FragmentedPacketGroup list.
		Alright. Lemme explain. This comment/to-do could really go in a bunch of places in SolarThing code, but I'm putting it here.
		We use the grouped list as a sort of easy way out for ever thinking about what grouping packets actually means.
		When we use packetGroupList below, it's mostly used for accumulated values that can be calculated using AccumulationUtil.
		There is really no reason that the packets *need* to be grouped to do that calculation.
		Remember that grouping packets could potentially throw out some packets if a fragment with less priority has packets stored more frequently.
		It probably doesn't matter here, but it's worth considering changing how we do stuff like this in the future.
		 */

		FragmentedPacketGroup latestPacketGroup = packetGroupList.get(packetGroupList.size() - 1);
		LOGGER.debug("Continuing with the latest packet group. Day start: " + dayStartTimeMillis);
		AddStatusParametersBuilder addStatusParametersBuilder = createStatusBuilder(zoneId, latestPacketGroup.getDateMillis());
		setStatusPowerValues(addStatusParametersBuilder, latestPacketGroup);
		setStatusEnergyValues(
				addStatusParametersBuilder,
				packetGroupList,
				AccumulationConfig.createDefault(dayStartTimeMillis)
		);
		DataProvider.Result resultVoltage = voltageProvider.getResult(latestPacketGroup);
		if (resultVoltage != null) {
			addStatusParametersBuilder.setVoltage(resultVoltage.getValue());
			// some variables we debug may be null (depending on VoltageProvider implementation), but that's OK
			LOGGER.debug("[status parameters] added voltage using identifier fragment: " + resultVoltage.getIdentifierFragment() + " from dateMillis: " + resultVoltage.getDateMillis());
		}
		DataProvider.Result resultTemperatureCelsius = temperatureCelsiusProvider.getResult(latestPacketGroup);
		if (resultTemperatureCelsius != null) {
			long goodReadingStartDateMillis = latestPacketGroup.getDateMillis() - 60 * 60 * 1000;
			float temperatureCelsius = resultTemperatureCelsius.getValue();
			if (!resultTemperatureCelsius.isPossiblyBadData() || isGoodReading(temperatureCelsiusProvider, temperatureCelsius, packetGroupList, goodReadingStartDateMillis)) {
				addStatusParametersBuilder.setTemperatureCelsius(temperatureCelsius);
				LOGGER.debug("[status parameters] added temperature using " + resultTemperatureCelsius.getIdentifierFragment() + " from dateMillis: " + resultTemperatureCelsius.getDateMillis());
			} else {
				LOGGER.info("[status parameters] Not setting temperature: " + temperatureCelsius + " because it could be a bad reading. From: " + resultTemperatureCelsius.getIdentifierFragment() + " from dateMillis: " + resultTemperatureCelsius.getDateMillis());
			}
		}
		return addStatusParametersBuilder.build();
	}
	private static boolean isGoodReading(TemperatureCelsiusProvider temperatureCelsiusProvider, float temperatureCelsius, List<FragmentedPacketGroup> packetGroupList, long startDateMillis) {
		SortedSet<Float> uniqueReadings = new TreeSet<>();
		for (FragmentedPacketGroup packetGroup : packetGroupList) {
			if (packetGroup.getDateMillis() < startDateMillis) {
				continue;
			}
			DataProvider.Result resultTemperatureCelsius = temperatureCelsiusProvider.getResult(packetGroup);
			if (resultTemperatureCelsius != null) {
				uniqueReadings.add(resultTemperatureCelsius.getValue());
			}
		}
		int removeAmount = uniqueReadings.size() / 7 + 1; // remove at least 1 set of outliers. For every 7 unique readings, remove another set
		for (int i = 0; i < removeAmount; i++) {
			// remove outliers
			uniqueReadings.remove(uniqueReadings.first());
			if (!uniqueReadings.isEmpty()) { // need to make this check because it's possible that the size is 1 before removing the first element
				uniqueReadings.remove(uniqueReadings.last());
			}
		}
		if (uniqueReadings.isEmpty()) {
			// better safe than sorry
			return false;
		}
		float firstReading = uniqueReadings.first();
		float lastReading = uniqueReadings.last();
		return temperatureCelsius > firstReading - 4.0f && temperatureCelsius < lastReading + 4.0f;
	}
	private static AddStatusParametersBuilder createStatusBuilder(ZoneId zoneId, long dateMillis) {
		LocalDateTime localDateTime = Instant.ofEpochMilli(dateMillis).atZone(zoneId).toLocalDateTime();
		SimpleDate date = SimpleDate.fromTemporal(localDateTime);
		SimpleTime time = SimpleTime.fromTemporal(localDateTime);
		return new AddStatusParametersBuilder(date, time);
	}
	private static AddStatusParametersBuilder setStatusPowerValues(AddStatusParametersBuilder builder, PacketGroup latestPacketGroup){
		PowerUtil.Data data = PowerUtil.getPowerData(latestPacketGroup, PowerUtil.GeneratingType.PV_ONLY);
		return builder.setPowerGeneration(data.getGeneratingWatts())
				.setPowerConsumption(data.getConsumingWatts());
	}
	private static AddStatusParametersBuilder setStatusEnergyValues(AddStatusParametersBuilder builder, List<FragmentedPacketGroup> packetGroups, AccumulationConfig accumulationConfig) {
		Map<IdentifierFragment, List<AccumulationPair<DailyChargeController>>> dailyMap = AccumulationUtil.getAccumulationPairs(AccumulationUtil.mapPackets(DailyChargeController.class, packetGroups), accumulationConfig);
		Map<IdentifierFragment, List<AccumulationPair<DailyAdvancedChargeController>>> advancedMap = AccumulationUtil.getAccumulationPairs(AccumulationUtil.mapPackets(DailyAdvancedChargeController.class, packetGroups), accumulationConfig);
		Map<IdentifierFragment, List<AccumulationPair<DailyFXPacket>>> dailyFXMap = AccumulationUtil.getAccumulationPairs(AccumulationUtil.mapPackets(DailyFXPacket.class, packetGroups), accumulationConfig);

		if (!dailyMap.isEmpty()) { // energy produced
			float generationKWH = AccumulationCalc.getSumTotal(dailyMap.values(), FloatAccumulationValue.convert(DailyChargeController::getDailyKWH), FloatAccumulationValueFactory.getInstance()).getValue();
			builder.setEnergyGeneration(Math.round(generationKWH * 1000.0f));
		}
		if (!dailyFXMap.isEmpty() || !advancedMap.isEmpty()) { // energy consumed
			float consumptionKWH = AccumulationCalc.getSumTotal(advancedMap.values(), FloatAccumulationValue.convert(DailyAdvancedChargeController::getDailyKWHConsumption), FloatAccumulationValueFactory.getInstance()).getValue() +
					AccumulationCalc.getSumTotal(dailyFXMap.values(), FloatAccumulationValue.convert(dailyFXPacket -> dailyFXPacket.getInverterKWH() + dailyFXPacket.getBuyKWH() - dailyFXPacket.getChargerKWH()), FloatAccumulationValueFactory.getInstance()).getValue();

			if (consumptionKWH >= 0) {// This if statement should almost always be executed, however, there are rare cases where (buy - charger) is negative for FX packets only
				builder.setEnergyConsumption(Math.round(consumptionKWH * 1000.0f));
			}
		}
		return builder;
	}
	public static AddOutputParametersBuilder setImportedExported(AddOutputParametersBuilder builder, List<FragmentedPacketGroup> packetGroups, AccumulationConfig accumulationConfig, boolean includeImport, boolean includeExport) {
		Map<IdentifierFragment, List<AccumulationPair<DailyFXPacket>>> dailyFXMap = AccumulationUtil.getAccumulationPairs(AccumulationUtil.mapPackets(DailyFXPacket.class, packetGroups), accumulationConfig);
		if (!dailyFXMap.isEmpty()) {
			if (includeImport) {
				float importKWH = AccumulationCalc.getSumTotal(dailyFXMap.values(), FloatAccumulationValue.convert(FXDailyData::getBuyKWH), FloatAccumulationValueFactory.getInstance()).getValue();
				builder.setImportOffPeak(Math.round(importKWH * 1000.0f));
			}
			if (includeExport) {
				float exportKWH = AccumulationCalc.getSumTotal(dailyFXMap.values(), FloatAccumulationValue.convert(FXDailyData::getSellKWH), FloatAccumulationValueFactory.getInstance()).getValue();
				builder.setExported(Math.round(exportKWH * 1000.0f));
			}
		}

		return builder;
	}

}
