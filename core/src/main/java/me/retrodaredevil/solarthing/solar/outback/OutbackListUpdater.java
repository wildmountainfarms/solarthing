package me.retrodaredevil.solarthing.solar.outback;

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
import me.retrodaredevil.solarthing.util.integration.MutableIntegral;
import me.retrodaredevil.solarthing.util.integration.TrapezoidalRuleAccumulator;
import me.retrodaredevil.solarthing.util.time.TimeIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private final TimeIdentifier timeIdentifier;
	private final PacketListReceiver eventReceiver;

	private final Map<Identifier, FXListUpdater> fxMap = new HashMap<>();
	private final Map<Identifier, MXListUpdater> mxMap = new HashMap<>();
	private Long lastTimeId = null;

	public OutbackListUpdater(TimeIdentifier timeIdentifier, PacketListReceiver eventReceiver) {
		this.timeIdentifier = requireNonNull(timeIdentifier);
		this.eventReceiver = requireNonNull(eventReceiver);
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

		for(Packet packet : new ArrayList<>(packets)){
			if(packet instanceof DocumentedPacket){
				DocumentedPacketType packetType = ((DocumentedPacket<?>) packet).getPacketType();
				if(packetType == SolarStatusPacketType.FX_STATUS){
					FXStatusPacket fx = (FXStatusPacket) packet;
					Identifier identifier = fx.getIdentifier();
					FXListUpdater updater = fxMap.get(identifier);
					if(updater == null){
						updater = new FXListUpdater(eventReceiver);
						fxMap.put(identifier, updater);
					}
					updater.update(now, packets, fx, wasInstant);
				} else if(packetType == SolarStatusPacketType.MXFM_STATUS){
					MXStatusPacket mx = (MXStatusPacket) packet;
					Identifier identifier = mx.getIdentifier();
					MXListUpdater updater = mxMap.get(identifier);
					if(updater == null){
						updater = new MXListUpdater(eventReceiver);
						mxMap.put(identifier, updater);
					}
					updater.update(now, packets, mx, wasInstant);
				}
			}
		}
	}
	private static MutableIntegral createMutableIntegral(){
		return new TrapezoidalRuleAccumulator();
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

		private FXListUpdater(PacketListReceiver eventReceiver) {
			this.eventReceiver = requireNonNull(eventReceiver);
		}

		private void update(long currentTimeMillis, List<? super Packet> packets, FXStatusPacket fx, boolean wasInstant){
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
	private static final class MXListUpdater {
		private final PacketListReceiver eventReceiver;
		private int errorMode = 0;
		private Long startDateMillis = null;
		private Float minimumBatteryVoltage = null;
		private Float maximumBatteryVoltage = null;

		private float accumulatedKWH = 0;
		private int accumulatedAH = 0;

		private MXStatusPacket lastMX = null;

		private MXListUpdater(PacketListReceiver eventReceiver) {
			this.eventReceiver = requireNonNull(eventReceiver);
		}

		private void update(long currentTimeMillis, List<? super Packet> packets, MXStatusPacket mx, boolean wasInstant){
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
