package me.retrodaredevil.solarthing.program.pvoutput;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragmentMatcher;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParametersBuilder;
import me.retrodaredevil.solarthing.solar.common.PVCurrentAndVoltage;
import me.retrodaredevil.solarthing.solar.daily.DailyCalc;
import me.retrodaredevil.solarthing.solar.daily.DailyConfig;
import me.retrodaredevil.solarthing.solar.daily.DailyPair;
import me.retrodaredevil.solarthing.solar.daily.DailyUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PVOutputHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PVOutputHandler.class);

	private final TimeZone timeZone;
	private final Map<Integer, List<String>> requiredIdentifierMap;
	private final IdentifierFragmentMatcher voltageIdentifierFragmentMatcher;

	public PVOutputHandler(TimeZone timeZone, Map<Integer, List<String>> requiredIdentifierMap, IdentifierFragmentMatcher voltageIdentifierFragmentMatcher) {
		this.timeZone = timeZone;
		this.requiredIdentifierMap = requiredIdentifierMap;
		this.voltageIdentifierFragmentMatcher = voltageIdentifierFragmentMatcher;
	}
	public boolean checkPackets(long dayStartTimeMillis, List<FragmentedPacketGroup> packetGroupList) {
		if (packetGroupList.isEmpty()) {
			LOGGER.warn("No packets!");
			return false;
		}
		FragmentedPacketGroup latestPacketGroup = packetGroupList.get(packetGroupList.size() - 1);
		outerLoop: for (Map.Entry<Integer, List<String>> entry : requiredIdentifierMap.entrySet()) {
			Integer desiredFragmentId = entry.getKey();
			if (!latestPacketGroup.hasFragmentId(desiredFragmentId)) {
				LOGGER.warn("The latest packet group doesn't contain the " + desiredFragmentId + " fragment id!");
				return false;
			}
			for (String desiredIdentifierRepresentation : entry.getValue()) {
				for (Packet packet : latestPacketGroup.getPackets()) {
					Integer fragmentId = latestPacketGroup.getFragmentId(packet);
					if (!Objects.equals(fragmentId, desiredFragmentId)) {
						continue;
					}
					if (packet instanceof Identifiable) {
						Identifier identifier = ((Identifiable) packet).getIdentifier();
						if (desiredIdentifierRepresentation.equals(identifier.getRepresentation())) {
							continue outerLoop;
						}
					}
				}
				LOGGER.warn("The required identifier: " + entry.getValue() + " with fragmentId: " + entry.getKey() + " was not present in the latest packet group!");
				return false;
			}
		}
		return true;
	}

	public AddStatusParameters getStatus(long dayStartTimeMillis, List<FragmentedPacketGroup> packetGroupList) {
		FragmentedPacketGroup latestPacketGroup = packetGroupList.get(packetGroupList.size() - 1);
		LOGGER.debug("Continuing with the latest packet group. Day start: " + dayStartTimeMillis);
		AddStatusParametersBuilder addStatusParametersBuilder = createStatusBuilder(timeZone, latestPacketGroup.getDateMillis());
		setStatusPowerValues(addStatusParametersBuilder, latestPacketGroup);
		setStatusEnergyValues(
				addStatusParametersBuilder,
				packetGroupList,
				new DailyConfig(dayStartTimeMillis + 3 * 60 * 60 * 1000, dayStartTimeMillis + 10 * 60 * 60 * 1000)
		);
		for (Packet packet : latestPacketGroup.getPackets()) {
			if (packet instanceof PVCurrentAndVoltage) {
				Integer fragmentId = latestPacketGroup.getFragmentId(packet);
				PVCurrentAndVoltage pvCurrentAndVoltage = (PVCurrentAndVoltage) packet;
				IdentifierFragment identifierFragment = IdentifierFragment.create(fragmentId, pvCurrentAndVoltage.getIdentifier());
				if (voltageIdentifierFragmentMatcher.matches(identifierFragment)) {
					float voltage = pvCurrentAndVoltage.getInputVoltage().floatValue();
					addStatusParametersBuilder.setVoltage(voltage);
					break;
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
		Integer generatingW = null;
		Integer usingW = null;
		for(Packet packet : latestPacketGroup.getPackets()){
			if(packet instanceof MXStatusPacket){
				if (generatingW == null) {
					generatingW = 0;
				}
				generatingW += ((MXStatusPacket) packet).getPVWattage();
			} else if(packet instanceof FXStatusPacket){
				if (usingW == null) {
					usingW = 0;
				}
				usingW += ((FXStatusPacket) packet).getPowerUsageWattage();
			} else if(packet instanceof RoverStatusPacket){
				if (generatingW == null) {
					generatingW = 0;
				}
				if (usingW == null) {
					usingW = 0;
				}
				generatingW += ((RoverStatusPacket) packet).getPVWattage().intValue();
				usingW += ((RoverStatusPacket) packet).getLoadPower();
			}
		}

		return builder.setPowerGeneration(generatingW)
				.setPowerConsumption(usingW);
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
		if (!dailyFXMap.isEmpty()) { // energy consumed // right now, only do this if there are fx dailies
			float consumptionKWH = DailyCalc.getSumTotal(roverMap.values(), RoverStatusPacket::getDailyKWHConsumption) +
					DailyCalc.getSumTotal(dailyFXMap.values(), dailyFXPacket -> dailyFXPacket.getInverterKWH() + dailyFXPacket.getBuyKWH() - dailyFXPacket.getChargerKWH());
			builder.setEnergyConsumption(Math.round(consumptionKWH * 1000.0f));
		}
		return builder;
	}

}
