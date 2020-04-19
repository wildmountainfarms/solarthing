package me.retrodaredevil.solarthing.solar.outback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.ImmutableFXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.event.ImmutableFXDayEndPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.ImmutableDailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.common.ImmutableMXDailyData;
import me.retrodaredevil.solarthing.solar.outback.mx.common.MXDailyData;
import me.retrodaredevil.solarthing.solar.outback.mx.event.ImmutableMXDayEndPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.ImmutableMXRawDayEndPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXRawDayEndPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.extra.DailyMXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.extra.ImmutableDailyMXPacket;
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
public class OutbackListUpdater implements PacketListReceiver {
	/*
	So you think this class is a mess? You would be correct! I plan to eventually refactor this class, but I didn't want to start
	the refactor process too early because if I did, I would eventually have to refactor the new mess I made.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OutbackListUpdater.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final TimeIdentifier timeIdentifier;
	private final PacketListReceiver eventReceiver;
	private final File fxJsonSaveFile;
	private final File mxJsonSaveFile;

	private final Map<Identifier, FXListUpdater> fxMap = new HashMap<>();
	private final Map<Identifier, MXListUpdater> mxMap = new HashMap<>();
	private Long lastTimeId = null;

	public OutbackListUpdater(TimeIdentifier timeIdentifier, PacketListReceiver eventReceiver, File dataDirectory) {
		this.timeIdentifier = requireNonNull(timeIdentifier);
		this.eventReceiver = requireNonNull(eventReceiver);
		fxJsonSaveFile = new File(dataDirectory, "fx_list_updater.json");
		mxJsonSaveFile = new File(dataDirectory, "mx_list_updater.json");
	}

	@Override
	public void receive(List<Packet> packets, boolean wasInstant) {
		long now = System.currentTimeMillis();
		long timeId = timeIdentifier.getTimeId(now);
		final Long lastTimeId = this.lastTimeId;
		this.lastTimeId = timeId;
		if(lastTimeId != null && lastTimeId != timeId){
			for(FXListUpdater updater : fxMap.values()){
				updater.doDayEnd(wasInstant);
			}
			for(MXListUpdater updater : mxMap.values()){
				updater.doDayEnd(wasInstant);
			}
			fxMap.clear();
			mxMap.clear();
		}
		final FXJsonSaveData fxSavedJsonData;
		final MXJsonSaveData mxSavedJsonData;
		if(lastTimeId == null){
			{
				FXJsonSaveData data = null;
				try {
					data = MAPPER.readValue(fxJsonSaveFile, FXJsonSaveData.class);
				} catch (IOException e) {
					LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Unable to read fx json data. That's OK though!", e);
				}
				fxSavedJsonData = data;
			}
			{
				MXJsonSaveData data = null;
				try {
					data = MAPPER.readValue(mxJsonSaveFile, MXJsonSaveData.class);
				} catch(IOException e){
					LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Unable to read mx json data. That's OK though!", e);
				}
				mxSavedJsonData = data;
			}
		} else {
			fxSavedJsonData = null;
			mxSavedJsonData = null;
		}

		List<DailyFXPacket> dailyFXPackets = new ArrayList<>();
		List<MXSaveNode> mxSaveNodes = new ArrayList<>();
		for(Packet packet : new ArrayList<>(packets)){
			if(packet instanceof DocumentedPacket){
				DocumentedPacketType packetType = ((DocumentedPacket<?>) packet).getPacketType();
				if(packetType == SolarStatusPacketType.FX_STATUS){
					FXStatusPacket fx = (FXStatusPacket) packet;
					Identifier identifier = fx.getIdentifier();
					FXListUpdater updater = fxMap.get(identifier);
					if(updater == null){
						final DailyFXPacket todayDailyFXPacket;
						{
							final DailyFXPacket storedDailyFXPacket = fxSavedJsonData == null ? null : fxSavedJsonData.getPacket(fx);
							if (storedDailyFXPacket == null) {
								todayDailyFXPacket = null;
								if(fxSavedJsonData != null){
									LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Although fxSavedJsonData isn't null we couldn't find a packet for identifier: {}", identifier);
								}
							} else {
								long startDateMillis = requireNonNull(storedDailyFXPacket.getStartDateMillis());
								if (timeIdentifier.getTimeId(startDateMillis) == timeId) {
									todayDailyFXPacket = storedDailyFXPacket;
									LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "We found a daily fx packet from today! identifier: {}", identifier);
								} else {
									todayDailyFXPacket = null;
									LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "The daily packet we found wasn't from today! identifier: {}", identifier);
								}
							}
						}
						updater = new FXListUpdater(eventReceiver, todayDailyFXPacket);
						fxMap.put(identifier, updater);
					}
					DailyFXPacket dailyFXPacket = updater.update(now, packets, fx, wasInstant);
					dailyFXPackets.add(dailyFXPacket);
				} else if(packetType == SolarStatusPacketType.MXFM_STATUS){
					MXStatusPacket mx = (MXStatusPacket) packet;
					Identifier identifier = mx.getIdentifier();
					MXListUpdater updater = mxMap.get(identifier);
					if(updater == null){
						final MXSaveNode todayNode;
						{
							final MXSaveNode storedSaveNode = mxSavedJsonData == null ? null : mxSavedJsonData.getNode(mx);
							if(storedSaveNode == null){
								todayNode = null;
								if(mxSavedJsonData != null){
									LOGGER.warn("Although fxSavedJsonData isn't null, we couldn't find a MXSaveNode for identifier: {}", identifier);
								}
							} else {
								long startDateMillis = requireNonNull(storedSaveNode.dailyMXPacket.getStartDateMillis());
								if(timeIdentifier.getTimeId(startDateMillis) == timeId) {
									todayNode = storedSaveNode;
									LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "We found a MXSaveNode from today! identifier: {}", identifier);
								} else {
									todayNode = null;
									LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "The MXSaveNode we found wasn't from today! identifier: {}", identifier);
								}
							}
						}
						updater = new MXListUpdater(eventReceiver, todayNode);
						mxMap.put(identifier, updater);
					}
					MXSaveNode node = updater.update(now, packets, mx, wasInstant);
					mxSaveNodes.add(node);
				}
			}
		}
		if(!dailyFXPackets.isEmpty()) {
			FXJsonSaveData fxJsonSaveData = new FXJsonSaveData(dailyFXPackets);
			try {
				MAPPER.writer().writeValue(fxJsonSaveFile, fxJsonSaveData);
			} catch (IOException e) {
				// in rare cases, the this happens and the message is "No space left on device". We could try reporting this in a more extreme way (maybe a packet or something)
				LOGGER.error("Unable to write to file!", e);
			}
		}
		if(!mxSaveNodes.isEmpty()) {
			MXJsonSaveData  mxJsonSaveData = new MXJsonSaveData(mxSaveNodes);
			try {
				MAPPER.writer().writeValue(mxJsonSaveFile, mxJsonSaveData);
			} catch (IOException e) {
				LOGGER.error("Unable to write to file!", e);
			}
		}
	}
	private static MutableIntegral createMutableIntegral(){
		return new TrapezoidalRuleAccumulator();
	}
	@JsonExplicit
	private static final class FXJsonSaveData {
		@JsonProperty
		private final List<DailyFXPacket> dailyFXPackets;

		@JsonCreator
		private FXJsonSaveData(
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
	private static final class FXListUpdater {
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

		private FXListUpdater(PacketListReceiver eventReceiver, DailyFXPacket storedDailyFXPacket) {
			this.eventReceiver = requireNonNull(eventReceiver);
			if(storedDailyFXPacket != null){
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Restored daily fx info from today! storedDailyFXPacket: {}", storedDailyFXPacket);
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
			DailyFXPacket packet = new ImmutableDailyFXPacket(createData(fx), fx.getIdentifier());

			packets.add(packet);
			this.lastFX = fx;
			return packet;
		}
		private FXDailyData createData(FXStatusPacket fx){
			return new ImmutableFXDailyData(
					fx.getAddress(),
					startDateMillis,
					minimumBatteryVoltage, maximumBatteryVoltage,
					(float) (inverterWH.getIntegral() / 1000), (float) (chargerWH.getIntegral() / 1000),
					(float) (buyWH.getIntegral() / 1000), (float) (sellWH.getIntegral() / 1000),
					operationalModeValues, errorMode, warningMode, misc, acModeValues
			);
		}
		private void doDayEnd(boolean wasInstant){
			FXStatusPacket fx = requireNonNull(lastFX);
			eventReceiver.receive(Collections.singletonList(new ImmutableFXDayEndPacket(createData(fx), fx.getIdentifier())), wasInstant);
		}
	}
	@JsonExplicit
	private static final class MXJsonSaveData {
		@JsonProperty
		private final List<MXSaveNode> mxNodes;

		@JsonCreator
		private MXJsonSaveData(@JsonProperty(value = "mxNodes", required = true) @JsonDeserialize(as = ArrayList.class) List<MXSaveNode> mxNodes) {
			this.mxNodes = mxNodes;
		}
		public MXSaveNode getNode(MXStatusPacket mx){
			Identifier identifier = mx.getIdentifier();
			for(MXSaveNode node : mxNodes){
				if(node.dailyMXPacket.getIdentifier().getSupplementaryTo().equals(identifier)){
					return node;
				}
			}
			return null;
		}
	}
	@JsonExplicit
	private static final class MXSaveNode {
		@JsonProperty
		private final DailyMXPacket dailyMXPacket;
		@JsonProperty
		private final float accumulatedKWH;
		@JsonProperty
		private final int accumulatedAH;

		@JsonCreator
		private MXSaveNode(
				@JsonProperty(value = "dailyMXPacket", required = true) DailyMXPacket dailyMXPacket,
				@JsonProperty(value = "accumulatedKWH", required = true) float accumulatedKWH,
				@JsonProperty(value = "accumulatedAH", required = true) int accumulatedAH) {
			this.dailyMXPacket = dailyMXPacket;
			this.accumulatedKWH = accumulatedKWH;
			this.accumulatedAH = accumulatedAH;
		}
	}
	private static final class MXListUpdater {
		private final PacketListReceiver eventReceiver;
		private int errorMode = 0;
		private Long startDateMillis = null;
		private Float minimumBatteryVoltage = null;
		private Float maximumBatteryVoltage = null;

		private float accumulatedKWH = 0;
		private int accumulatedAH = 0;

		private MXStatusPacket lastMX = null;

		private MXListUpdater(PacketListReceiver eventReceiver, MXSaveNode storedSaveNode) {
			this.eventReceiver = requireNonNull(eventReceiver);
			if(storedSaveNode != null){
				DailyMXPacket packet = storedSaveNode.dailyMXPacket;
				errorMode = packet.getErrorModeValue();
				startDateMillis = packet.getStartDateMillis();
				minimumBatteryVoltage = packet.getDailyMinBatteryVoltage();
				maximumBatteryVoltage = packet.getDailyMaxBatteryVoltage();

				accumulatedKWH = storedSaveNode.accumulatedKWH;
				accumulatedAH = storedSaveNode.accumulatedAH;
			}
		}

		private MXSaveNode update(long currentTimeMillis, List<? super Packet> packets, MXStatusPacket mx, boolean wasInstant){
			final MXStatusPacket last = this.lastMX;
			this.lastMX = mx;

			if(last != null && mx.isNewDay(last)){
				MXRawDayEndPacket dayEndPacket = new ImmutableMXRawDayEndPacket(last.getAddress(), last.getDailyKWH(), last.getDailyAH(), last.getDailyAHSupport());
				eventReceiver.receive(Collections.singletonList(dayEndPacket), wasInstant);
			}

			Long startDateMillis = this.startDateMillis;
			if(startDateMillis == null){
				startDateMillis = currentTimeMillis;
				this.startDateMillis = startDateMillis;
			}

			float batteryVoltage = mx.getBatteryVoltage();
			Float currentMin = minimumBatteryVoltage;
			Float currentMax = maximumBatteryVoltage;
			if(currentMin == null || batteryVoltage < currentMin){
				minimumBatteryVoltage = batteryVoltage;
			}
			if(currentMax == null || batteryVoltage > currentMax){
				maximumBatteryVoltage = batteryVoltage;
			}
			errorMode |= mx.getErrorModeValue();

			if(last != null){
				float lastKWH = last.getDailyKWH();
				int lastAH = last.getDailyAH();
				float kwh = mx.getDailyKWH();
				int ah = mx.getDailyAH();
				if(kwh > lastKWH){
					accumulatedKWH += kwh - lastKWH;
				}
				if(ah > lastAH){
					accumulatedAH += ah - lastAH;
				}
			}
			MXDailyData data = createData(mx);
			DailyMXPacket packet = new ImmutableDailyMXPacket(data, mx.getIdentifier());
			packets.add(packet);

			return new MXSaveNode(packet, accumulatedKWH, accumulatedAH);
		}
		private MXDailyData createData(MXStatusPacket mx){
			return new ImmutableMXDailyData(mx.getAddress(), errorMode, startDateMillis, accumulatedKWH, accumulatedAH, mx.getDailyAHSupport(), minimumBatteryVoltage, maximumBatteryVoltage);
		}

		private void doDayEnd(boolean wasInstant) {
			MXStatusPacket mx = requireNonNull(lastMX);
			eventReceiver.receive(Collections.singletonList(new ImmutableMXDayEndPacket(createData(mx), mx.getIdentifier())), wasInstant);
		}
	}
}
