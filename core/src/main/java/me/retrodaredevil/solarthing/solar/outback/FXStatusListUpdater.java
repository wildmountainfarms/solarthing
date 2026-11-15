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
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.ImmutableDailyFXPacket;
import me.retrodaredevil.solarthing.util.integration.MutableIntegral;
import me.retrodaredevil.solarthing.util.integration.TrapezoidalRuleAccumulator;
import me.retrodaredevil.solarthing.util.time.TimeIdentifier;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.InstantSource;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Takes a list of {@link Packet}s and will add an {@link DailyFXPacket} for each {@link FXStatusPacket}.
 * <p>
 * This expects that there are no duplicate packets. If there are duplicate packets, the behaviour is undefined.
 */
@NullMarked
public class FXStatusListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(FXStatusListUpdater.class);

	private final TimeIdentifier timeIdentifier;
	private final InstantSource instantSource;

	private final Map<Identifier, FXListUpdater> fxMap = new HashMap<>();
	private @Nullable Long lastTimeId = null;

	public FXStatusListUpdater(TimeIdentifier timeIdentifier, InstantSource instantSource) {
		this.timeIdentifier = requireNonNull(timeIdentifier);
		this.instantSource = requireNonNull(instantSource);
	}

	@Override
	public void receive(List<Packet> packets) {
		Instant now = instantSource.instant();

		long timeId = timeIdentifier.getTimeId(now);
		final Long lastTimeId = this.lastTimeId;
		this.lastTimeId = timeId;
		if(lastTimeId != null && lastTimeId != timeId){
			fxMap.clear();
		}

		for(Packet packet : new ArrayList<>(packets)){
			if(packet instanceof DocumentedPacket documentedPacket){
				DocumentedPacketType packetType = documentedPacket.getPacketType();
				if(packetType == SolarStatusPacketType.FX_STATUS){
					FXStatusPacket fx = (FXStatusPacket) packet;
					Identifier identifier = fx.getIdentifier();
					FXListUpdater updater = fxMap.get(identifier);
					if(updater == null){
						updater = new FXListUpdater();
						fxMap.put(identifier, updater);
					}
					updater.update(now, packets, fx);
				}
			}
		}
	}
	private static MutableIntegral createMutableIntegral(){
		return new TrapezoidalRuleAccumulator();
	}
	private static final class FXListUpdater {
		private @Nullable Instant startTime = null;
		private @Nullable Float minimumBatteryVoltage = null;
		private @Nullable Float maximumBatteryVoltage = null;

		private final MutableIntegral inverterWH = createMutableIntegral();
		private final MutableIntegral chargerWH = createMutableIntegral();
		private final MutableIntegral buyWH = createMutableIntegral();
		private final MutableIntegral sellWH = createMutableIntegral();

		private final Set<Integer> operationalModeValues = new HashSet<>();
		private int errorMode = 0;
		private int warningMode = 0;
		private int misc = 0;
		private final Set<Integer> acModeValues = new HashSet<>();

		private void update(Instant currentTime, List<? super Packet> packets, FXStatusPacket fx){
			Instant startTime = this.startTime;
			if(startTime == null){
				startTime = currentTime;
				this.startTime = startTime;
			}

			float batteryVoltage = fx.getBatteryVoltage();
			// TODO submit bug report to NullAway - we should be able to store field value in local variable before doing comparison
//			Float currentMin = minimumBatteryVoltage;
//			Float currentMax = maximumBatteryVoltage;
			if(minimumBatteryVoltage == null || batteryVoltage < minimumBatteryVoltage){
				minimumBatteryVoltage = batteryVoltage;
			}
			if(maximumBatteryVoltage == null || batteryVoltage > maximumBatteryVoltage){
				maximumBatteryVoltage = batteryVoltage;
			}

			try {
				inverterWH.add(currentTime, fx.getInverterWattage());
				chargerWH.add(currentTime, fx.getChargerWattage());
				buyWH.add(currentTime, fx.getBuyWattage());
				sellWH.add(currentTime, fx.getSellWattage());
			} catch (IllegalArgumentException e) {
				LOGGER.info("The clock must have changed on us!", e);
			}

			operationalModeValues.add(fx.getOperationalModeValue());
			errorMode |= fx.getErrorModeValue();
			warningMode |= fx.getWarningModeValue();
			misc |= fx.getMiscValue();
			acModeValues.add(fx.getACModeValue());
			DailyFXPacket packet = new ImmutableDailyFXPacket(createData(fx), fx.getIdentifier());

			packets.add(packet);
		}
		@RequiresNonNull({"minimumBatteryVoltage", "maximumBatteryVoltage", "startTime"})
		private FXDailyData createData(FXStatusPacket fx){
			return new ImmutableFXDailyData(
					fx.getAddress(),
					startTime.toEpochMilli(),
					minimumBatteryVoltage, maximumBatteryVoltage,
					(float) (inverterWH.getIntegral() / 1000), (float) (chargerWH.getIntegral() / 1000),
					(float) (buyWH.getIntegral() / 1000), (float) (sellWH.getIntegral() / 1000),
					operationalModeValues, errorMode, warningMode, misc, acModeValues
			);
		}
	}
}
