package me.retrodaredevil.solarthing.solar.outback.fx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.outback.fx.common.ImmutableFXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.event.ImmutableFXACModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.ImmutableDailyFXPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.integration.MutableIntegral;
import me.retrodaredevil.solarthing.util.integration.TrapezoidalRuleAccumulator;
import me.retrodaredevil.solarthing.util.time.TimeIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Takes a list of {@link Packet}s and will add an {@link DailyFXPacket} for each {@link FXStatusPacket}.
 * <p>
 * This expects that there are no duplicate packets. If there are duplicate packets, the behaviour is undefined.
 */
public class FXListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(FXListUpdater.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final TimeIdentifier timeIdentifier;
	private final PacketListReceiver eventReceiver;
	private final File jsonSaveFile;

	private final Map<Identifier, ListUpdater> map = new HashMap<>();
	private Long lastTimeId = null;

	public FXListUpdater(TimeIdentifier timeIdentifier, PacketListReceiver eventReceiver, File dataDirectory) {
		this.timeIdentifier = timeIdentifier;
		this.eventReceiver = eventReceiver;
		jsonSaveFile = new File(dataDirectory, "fx_list_updater.json");
	}

	@Override
	public void receive(List<Packet> packets, boolean wasInstant) {
		long now = System.currentTimeMillis();
		long timeId = timeIdentifier.getTimeId(now);
		final Long lastTimeId = this.lastTimeId;
		this.lastTimeId = timeId;
		if(lastTimeId != null && lastTimeId != timeId){
			// TODO do day end packets here
			map.clear();
		}
		final JsonSaveData savedJsonData;
		if(lastTimeId == null){
			JsonSaveData data = null;
			try {
				data = MAPPER.readValue(jsonSaveFile, JsonSaveData.class);
			} catch (IOException e) {
				LOGGER.info("Unable to read json data. That's OK though!", e);
			}
			savedJsonData = data;
		} else {
			savedJsonData = null;
		}

		List<DailyFXPacket> dailyFXPackets = new ArrayList<>();
		for(Packet packet : new ArrayList<>(packets)){
			if(packet instanceof DocumentedPacket){
				if(((DocumentedPacket<?>) packet).getPacketType() == SolarStatusPacketType.FX_STATUS){
					FXStatusPacket fx = (FXStatusPacket) packet;
					Identifier identifier = fx.getIdentifier();
					ListUpdater updater = map.get(identifier);
					if(updater == null){
						final DailyFXPacket todayDailyFXPacket;
						{
							final DailyFXPacket storedDailyFXPacket = savedJsonData == null ? null : savedJsonData.getPacket(fx);
							if (storedDailyFXPacket == null) {
								todayDailyFXPacket = null;
								if(savedJsonData != null){
									LOGGER.warn("Although savedJsonData isn't null we couldn't find a packet for identifier: {}", identifier);
								}
							} else {
								long startDateMillis = requireNonNull(storedDailyFXPacket.getStartDateMillis());
								if (timeIdentifier.getTimeId(startDateMillis) == timeId) {
									todayDailyFXPacket = storedDailyFXPacket;
									LOGGER.info("We found a daily fx packet from today! identifier: {}", identifier);
								} else {
									todayDailyFXPacket = null;
									LOGGER.info("The daily packet we found wasn't from today! identifier: {}", identifier);
								}
							}
						}
						updater = new ListUpdater(eventReceiver, todayDailyFXPacket);
						map.put(identifier, updater);
					}
					DailyFXPacket dailyFXPacket = updater.update(now, packets, fx, wasInstant);
					dailyFXPackets.add(dailyFXPacket);
				}
			}
		}
		if(!dailyFXPackets.isEmpty()) {
			JsonSaveData jsonSaveData = new JsonSaveData(dailyFXPackets);
			try {
				MAPPER.writer().writeValue(jsonSaveFile, jsonSaveData);
			} catch (IOException e) {
				LOGGER.error("Unable to write to file!", e);
			}
		}
	}
	private static MutableIntegral createMutableIntegral(){
		return new TrapezoidalRuleAccumulator();
	}
	@JsonExplicit
	private static final class JsonSaveData {
		@JsonProperty
		private final List<DailyFXPacket> dailyFXPackets;

		@JsonCreator
		private JsonSaveData(
				@JsonDeserialize(as = ArrayList.class) @JsonProperty(value = "dailyFXPackets", required = true) List<DailyFXPacket> dailyFXPackets
		) {
			this.dailyFXPackets = dailyFXPackets;
		}
		public DailyFXPacket getPacket(FXStatusPacket fx){
			Identifier identifier = fx.getIdentifier();
			for(DailyFXPacket packet : dailyFXPackets){
				if(packet.getIdentifier().getSupplementaryTo().equals(identifier)){
					return packet;
				}
			}
			return null;
		}
	}
	private static final class ListUpdater {
		private final PacketListReceiver eventReceiver;
		private Long startDateMillis = null;
		private Float minimumBatteryVoltage = null;
		private Float maximumBatteryVoltage = null;

		private final MutableIntegral inverterWH = createMutableIntegral();
		private final MutableIntegral chargerWH = createMutableIntegral();
		private final MutableIntegral buyWH = createMutableIntegral();
		private final MutableIntegral sellWH = createMutableIntegral();

		private final Set<Integer> operationalModeValues = new HashSet<>();
		private int errorMode = 0;
		private int warningMode = 0;
		private int misc = 0;
		private final Set<Integer> acModeValues = new HashSet<>();

		private FXStatusPacket lastFX = null;

		private ListUpdater(PacketListReceiver eventReceiver, DailyFXPacket storedDailyFXPacket) {
			this.eventReceiver = requireNonNull(eventReceiver);
			if(storedDailyFXPacket != null){
				LOGGER.info("Restored daily fx info from today! storedDailyFXPacket: {}", storedDailyFXPacket);
				startDateMillis = storedDailyFXPacket.getStartDateMillis();
				minimumBatteryVoltage = storedDailyFXPacket.getDailyMinBatteryVoltage();
				maximumBatteryVoltage = storedDailyFXPacket.getDailyMaxBatteryVoltage();

				inverterWH.setIntegral(storedDailyFXPacket.getInverterKWH() * 1000.0);
				chargerWH.setIntegral(storedDailyFXPacket.getChargerKWH() * 1000.0);
				buyWH.setIntegral(storedDailyFXPacket.getBuyKWH() * 1000.0);
				sellWH.setIntegral(storedDailyFXPacket.getSellKWH() * 1000.0);

				operationalModeValues.addAll(storedDailyFXPacket.getOperationalModeValues());
				errorMode = storedDailyFXPacket.getErrorModeValue();
				warningMode = storedDailyFXPacket.getWarningModeValue();
				misc = storedDailyFXPacket.getMiscValue();
				acModeValues.addAll(storedDailyFXPacket.getACModeValues());
			}
		}

		private DailyFXPacket update(long currentTimeMillis, List<? super Packet> packets, FXStatusPacket fx, boolean wasInstant){
			Long startDateMillis = this.startDateMillis;
			if(startDateMillis == null){
				startDateMillis = currentTimeMillis;
				this.startDateMillis = startDateMillis;
			}

			float batteryVoltage = fx.getBatteryVoltage();
			Float currentMin = minimumBatteryVoltage;
			Float currentMax = maximumBatteryVoltage;
			if(currentMin == null || batteryVoltage < currentMin){
				minimumBatteryVoltage = batteryVoltage;
			}
			if(currentMax == null || batteryVoltage > currentMax){
				maximumBatteryVoltage = batteryVoltage;
			}

			double hours = currentTimeMillis / (1000.0 * 60 * 60);
			inverterWH.add(hours, fx.getInverterWattage());
			chargerWH.add(hours, fx.getChargerWattage());
			buyWH.add(hours, fx.getBuyWattage());
			sellWH.add(hours, fx.getSellWattage());

			operationalModeValues.add(fx.getOperationalModeValue());
			errorMode |= fx.getErrorModeValue();
			warningMode |= fx.getWarningModeValue();
			misc |= fx.getMiscValue();
			acModeValues.add(fx.getACModeValue());
			DailyFXPacket packet = new ImmutableDailyFXPacket(
					new ImmutableFXDailyData(
							fx.getAddress(),
							startDateMillis,
							minimumBatteryVoltage, maximumBatteryVoltage,
							(float) (inverterWH.getIntegral() / 1000), (float) (chargerWH.getIntegral() / 1000),
							(float) (buyWH.getIntegral() / 1000), (float) (sellWH.getIntegral() / 1000),
							operationalModeValues, errorMode, warningMode, misc, acModeValues
					),
					fx.getIdentifier()
			);

			packets.add(packet);
			doLastFX(fx, wasInstant);
			return packet;
		}
		private void doLastFX(FXStatusPacket fx, boolean wasInstant){
			final FXStatusPacket lastFX = this.lastFX;
			this.lastFX = fx;
			final Integer lastACMode;
			if(lastFX == null){
				lastACMode = null;
			} else {
				lastACMode = lastFX.getACModeValue();
			}
			int currentACMode = fx.getACModeValue();
			if(lastACMode == null || currentACMode != lastACMode){
				eventReceiver.receive(Collections.singletonList(new ImmutableFXACModeChangePacket(fx.getIdentifier(), currentACMode, lastACMode)), wasInstant);
			}
		}
	}
}
