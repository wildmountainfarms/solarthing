package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;
import me.retrodaredevil.solarthing.solar.outback.fx.common.ImmutableFXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.event.ImmutableFXACModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.ImmutableDailyFXPacket;
import me.retrodaredevil.solarthing.util.integration.MutableIntegral;
import me.retrodaredevil.solarthing.util.integration.TrapezoidalRuleAccumulator;
import me.retrodaredevil.solarthing.util.scheduler.IterativeScheduler;

import java.util.*;

/**
 * Takes a list of {@link Packet}s and will add an {@link DailyFXPacket} for each {@link FXStatusPacket}.
 * <p>
 * This expects that there are no duplicate packets. If there are duplicate packets, the behaviour is undefined.
 */
public class DailyFXListUpdater implements PacketListReceiver {

	private final IterativeScheduler iterativeScheduler;
	private final PacketListReceiver eventReceiver;

	private final Map<Identifier, ListUpdater> map = new HashMap<>();

	public DailyFXListUpdater(IterativeScheduler iterativeScheduler, PacketListReceiver eventReceiver) {
		this.iterativeScheduler = iterativeScheduler;
		this.eventReceiver = eventReceiver;
	}

	@Override
	public void receive(List<Packet> packets, boolean wasInstant) {
		if(iterativeScheduler.shouldRun()){
			map.clear();
		}
		for(Packet packet : new ArrayList<>(packets)){
			if(packet instanceof DocumentedPacket){
				if(((DocumentedPacket<?>) packet).getPacketType() == SolarStatusPacketType.FX_STATUS){
					FXStatusPacket fx = (FXStatusPacket) packet;
					Identifier identifier = fx.getIdentifier();
					ListUpdater updater = map.get(identifier);
					if(updater == null){
						updater = new ListUpdater(eventReceiver);
						map.put(identifier, updater);
					}
					updater.update(packets, fx, wasInstant);
				}
			}
		}
	}
	private static MutableIntegral createMutableIntegral(){
		return new TrapezoidalRuleAccumulator();
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

		private ListUpdater(PacketListReceiver eventReceiver) {
			this.eventReceiver = eventReceiver;
		}

		private void update(List<? super Packet> packets, FXStatusPacket fx, boolean wasInstant){
			Long startDateMillis = this.startDateMillis;
			if(startDateMillis == null){
				startDateMillis = System.currentTimeMillis();
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

			double hours = getHours();
			inverterWH.add(hours, fx.getInverterWattage());
			chargerWH.add(hours, fx.getChargerWattage());
			buyWH.add(hours, fx.getBuyWattage());
			sellWH.add(hours, fx.getSellWattage());

			operationalModeValues.add(fx.getOperationalModeValue());
			errorMode |= fx.getErrorModeValue();
			warningMode |= fx.getWarningModeValue();
			misc |= fx.getMiscValue();
			acModeValues.add(fx.getACModeValue());
			Packet packet = new ImmutableDailyFXPacket(
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
			if(!Objects.equals(currentACMode, lastACMode)){
				eventReceiver.receive(Collections.singletonList(new ImmutableFXACModeChangePacket(fx.getIdentifier(), currentACMode, lastACMode)), wasInstant);
			}
		}

		private double getHours(){
			return System.currentTimeMillis() / (1000.0 * 60 * 60);
		}
	}
}
