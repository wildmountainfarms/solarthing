package me.retrodaredevil.solarthing.program.pvoutput;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParametersBuilder;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputService;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.daily.DailyConfig;
import me.retrodaredevil.solarthing.solar.daily.DailyPair;
import me.retrodaredevil.solarthing.solar.daily.DailyUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

public class PVOutputHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PVOutputHandler.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final TimeZone timeZone;
	private final List<Integer> requiredFragmentIds;
	private final Map<Integer, String> requiredIdentifiers;
	private final PVOutputService service;

	public PVOutputHandler(TimeZone timeZone, List<Integer> requiredFragmentIds, Map<Integer, String> requiredIdentifiers, PVOutputService service) {
		this.timeZone = timeZone;
		this.requiredFragmentIds = requiredFragmentIds;
		this.requiredIdentifiers = requiredIdentifiers;
		this.service = service;
	}

	public void handle(long dayStartTimeMillis, List<FragmentedPacketGroup> packetGroupList) {
		if (packetGroupList.isEmpty()) {
			LOGGER.warn("No packets!");
			return;
		}
		FragmentedPacketGroup latestPacketGroup = packetGroupList.get(packetGroupList.size() - 1);
		if (latestPacketGroup.getDateMillis() < System.currentTimeMillis() - 5 * 60 * 1000) {
			LOGGER.warn("The last packet is more than 5 minutes in the past!");
			return;
		}
		outerLoop: for (Integer fragmentId : requiredFragmentIds) {
			for (Packet packet : latestPacketGroup.getPackets()) {
				if (Objects.equals(fragmentId, latestPacketGroup.getFragmentId(packet))) {
					continue outerLoop;
				}
			}
			LOGGER.warn("The required fragmentId: " + fragmentId + " was not present in the latest packet group!");
			return;
		}
		outerLoop: for (Map.Entry<Integer, String> entry : requiredIdentifiers.entrySet()) {
			for (Packet packet : latestPacketGroup.getPackets()) {
				if (packet instanceof Identifiable) {
					Identifier identifier = ((Identifiable) packet).getIdentifier();
					Integer fragmentId = latestPacketGroup.getFragmentId(packet);
					if (Objects.equals(fragmentId, entry.getKey()) && identifier.getRepresentation().equals(entry.getValue())) {
						continue outerLoop;
					}
				}
			}
			LOGGER.warn("The required identifier: " + entry.getValue() + " with fragmentId: " + entry.getKey() + " was not present int he latest packet group!");
			return;
		}
		LOGGER.debug("Continuing with the latest packet group. Day start: " + dayStartTimeMillis);
		AddStatusParametersBuilder addStatusParametersBuilder = createBuilder(timeZone, latestPacketGroup.getDateMillis());
		setPowerValues(addStatusParametersBuilder, latestPacketGroup);
		setEnergyValues(
				addStatusParametersBuilder,
				packetGroupList,
				new DailyConfig(dayStartTimeMillis + 12 * 60 * 60 * 1000)
		);
		AddStatusParameters parameters = addStatusParametersBuilder.build();
		try {
			LOGGER.debug(MAPPER.writeValueAsString(parameters));
		} catch (JsonProcessingException e) {
			LOGGER.error("Got error serializing JSON. This should never happen.", e);
		}
		Call<String> call = service.addStatus(parameters);
		LOGGER.debug("Executing call");
		Response<String> response = null;
		try {
			response = call.execute();
		} catch (IOException e) {
			LOGGER.error("Exception while executing", e);
		}
		if(response != null) {
			if (response.isSuccessful()) {
				LOGGER.debug("Executed successfully. Result: " + response.body());
			} else {
				LOGGER.debug("Unsuccessful. Message: " + response.message() + " code: " + response.code());
			}
		}
	}
	private static AddStatusParametersBuilder createBuilder(TimeZone timeZone, long dateMillis) {
		Calendar calendar = new GregorianCalendar(timeZone);
		calendar.setTimeInMillis(dateMillis);
		SimpleDate date = SimpleDate.fromCalendar(calendar);
		SimpleTime time = SimpleTime.fromCalendar(calendar);
		return new AddStatusParametersBuilder(date, time);
	}
	private static AddStatusParametersBuilder setPowerValues(AddStatusParametersBuilder builder, PacketGroup latestPacketGroup){
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
				usingW += ((FXStatusPacket) packet).getInverterWattage();
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
	private static AddStatusParametersBuilder setEnergyValues(AddStatusParametersBuilder builder, List<FragmentedPacketGroup> packetGroups, DailyConfig dailyConfig) {
		Map<IdentifierFragment, List<DailyPair<MXStatusPacket>>> mxMap = DailyUtil.getDailyPairs(DailyUtil.mapPackets(MXStatusPacket.class, packetGroups), dailyConfig);
		Map<IdentifierFragment, List<DailyPair<RoverStatusPacket>>> roverMap = DailyUtil.getDailyPairs(DailyUtil.mapPackets(RoverStatusPacket.class, packetGroups), dailyConfig);
		Map<IdentifierFragment, List<DailyPair<DailyFXPacket>>> dailyFXMap = DailyUtil.getDailyPairs(DailyUtil.mapPackets(DailyFXPacket.class, packetGroups), dailyConfig);
		if (!mxMap.isEmpty() || !roverMap.isEmpty()) { // energy produced
			builder.setEnergyGeneration(
					getTotal(mxMap.values(), MXStatusPacket::getDailyKWH) +
							getTotal(roverMap.values(), RoverStatusPacket::getDailyKWH)
			);
		}
		if (!roverMap.isEmpty() || !dailyFXMap.isEmpty()) { // energy consumed
			builder.setEnergyConsumption(
					getTotal(roverMap.values(), RoverStatusPacket::getDailyKWHConsumption) +
							getTotal(dailyFXMap.values(), dailyFXPacket -> dailyFXPacket.getInverterKWH() + dailyFXPacket.getBuyKWH() - dailyFXPacket.getChargerKWH())
			);
		}
		return builder;
	}
	private static <T extends Packet & DailyData> float getTotal(Collection<List<DailyPair<T>>> dailyPairListCollection, TotalGetter<T> totalGetter) {
		float total = 0;
		for (List<DailyPair<T>> dailyPairs : dailyPairListCollection) {
			for (DailyPair<T> dailyPair : dailyPairs) {
				if (dailyPair.getStartPacketType() == DailyPair.StartPacketType.CUT_OFF) {
					total += totalGetter.getTotal(dailyPair.getLatestPacket().getPacket()) - totalGetter.getTotal(dailyPair.getStartPacket().getPacket());
				} else {
					total += totalGetter.getTotal(dailyPair.getLatestPacket().getPacket());
				}
			}
		}
		return total;
	}
	private interface TotalGetter<T> {
		float getTotal(T t);
	}

}
