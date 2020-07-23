package me.retrodaredevil.solarthing.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.graphql.packets.*;
import me.retrodaredevil.solarthing.misc.device.CpuTemperaturePacket;
import me.retrodaredevil.solarthing.misc.weather.TemperaturePacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.solar.BatteryUtil;
import me.retrodaredevil.solarthing.solar.common.BasicChargeController;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import me.retrodaredevil.solarthing.solar.common.PVCurrentAndVoltage;
import me.retrodaredevil.solarthing.solar.outback.command.packets.SuccessMateCommandPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXACModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXAuxStateChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXOperationalModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXAuxModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXChargerModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXRawDayEndPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolarThingGraphQLService {

	private final SimpleQueryHandler simpleQueryHandler;

	public SolarThingGraphQLService(SimpleQueryHandler simpleQueryHandler) {
		this.simpleQueryHandler = simpleQueryHandler;
	}


	@GraphQLQuery
	public SolarThingStatusQuery queryStatus(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "sourceId") @Nullable String sourceId){
		List<? extends InstancePacketGroup> packets = simpleQueryHandler.queryStatus(from, to, sourceId);
		return new SolarThingStatusQuery(new BasicPacketGetter(packets, PacketFilter.KEEP_ALL), simpleQueryHandler.sortPackets(packets, sourceId));
	}
	@GraphQLQuery
	public SolarThingStatusQuery queryStatusLast(
			@GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "sourceId") @Nullable String sourceId, @GraphQLArgument(name = "reversed") Boolean reversed){
		List<? extends InstancePacketGroup> packets = simpleQueryHandler.queryStatus(to - 2 * 60 * 1000, to, sourceId);
		List<InstancePacketGroup> lastPackets = new ArrayList<>();
		for(List<InstancePacketGroup> packetGroups : PacketGroups.mapFragments(packets).values()) {
			lastPackets.add(packetGroups.get(packetGroups.size() - 1));
		}
		return new SolarThingStatusQuery(new ReversedPacketGetter(new BasicPacketGetter(lastPackets, PacketFilter.KEEP_ALL), Boolean.TRUE.equals(reversed)), simpleQueryHandler.sortPackets(lastPackets, sourceId));
	}
	@GraphQLQuery
	public SolarThingEventQuery queryEvent(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "sourceId") @Nullable String sourceId,
			@GraphQLArgument(name = "includeUnknownChangePackets", defaultValue = "false") boolean includeUnknownChangePackets
	) {
		return new SolarThingEventQuery(new BasicPacketGetter(simpleQueryHandler.queryEvent(from, to, sourceId), new UnknownChangePacketsFilter(includeUnknownChangePackets)));
	}
	@GraphQLQuery(description = "Queries events in the specified time range while only including the specified identifier in the specified fragment")
	public SolarThingEventQuery queryEventIdentifier(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "fragmentId") int fragmentId,
			@GraphQLArgument(name = "identifier") @NotNull String identifierRepresentation,
			@GraphQLArgument(name = "includeUnknownChangePackets", defaultValue = "false") boolean includeUnknownChangePackets
	) {
		return new SolarThingEventQuery(new BasicPacketGetter(
				simpleQueryHandler.queryEvent(from, to, null),
				new PacketFilterMultiplexer(Arrays.asList(new FragmentFilter(fragmentId), new IdentifierFilter(identifierRepresentation), new UnknownChangePacketsFilter(includeUnknownChangePackets)))
		));
	}
	@GraphQLQuery(description = "Queries events in the specified time range while only including the specified fragment")
	public SolarThingEventQuery queryEventFragment(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "fragmentId") int fragmentId,
			@GraphQLArgument(name = "includeUnknownChangePackets", defaultValue = "false") boolean includeUnknownChangePackets
	) {
		return new SolarThingEventQuery(new BasicPacketGetter(
				simpleQueryHandler.queryEvent(from, to, null),
				new PacketFilterMultiplexer(Arrays.asList(new FragmentFilter(fragmentId), new UnknownChangePacketsFilter(includeUnknownChangePackets)))
		));
	}
	public static class SolarThingStatusQuery {
		private final PacketGetter packetGetter;
		private final List<? extends FragmentedPacketGroup> sortedPackets;

		public SolarThingStatusQuery(PacketGetter packetGetter, List<? extends FragmentedPacketGroup> sortedPackets) {
			this.packetGetter = packetGetter;
			this.sortedPackets = sortedPackets;
		}

		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<BatteryVoltage>> batteryVoltage() {
			return packetGetter.getPackets(BatteryVoltage.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<FXStatusPacket>> fxStatus() {
			return packetGetter.getPackets(FXStatusPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<MXStatusPacket>> mxStatus() {
			return packetGetter.getPackets(MXStatusPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<DailyFXPacket>> fxDaily() {
			return packetGetter.getPackets(DailyFXPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<RoverStatusPacket>> roverStatus() {
			return packetGetter.getPackets(RoverStatusPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<PVCurrentAndVoltage>> solar() {
			return packetGetter.getPackets(PVCurrentAndVoltage.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<BasicChargeController>> chargeController() {
			return packetGetter.getPackets(BasicChargeController.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<DailyChargeController>> dailyChargeController() {
			return packetGetter.getPackets(DailyChargeController.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<CpuTemperaturePacket>> cpuTemperature() {
			return packetGetter.getPackets(CpuTemperaturePacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<TemperaturePacket>> temperature() {
			return packetGetter.getPackets(TemperaturePacket.class);
		}


		@GraphQLQuery
		public @NotNull List<@NotNull DataNode<Float>> batteryVoltageTemperatureCompensated() {
			List<DataNode<Float>> r = new ArrayList<>();
			for (FragmentedPacketGroup packetGroup : sortedPackets) {
				RoverStatusPacket rover = null;
				for (Packet packet : packetGroup.getPackets()) {
					if (packet instanceof RoverStatusPacket) {
						rover = (RoverStatusPacket) packet;
					}
				}
				if (rover == null) {
					continue;
				}
				int temperatureCelsius = rover.getBatteryTemperatureCelsius();
				for (Packet packet : packetGroup.getPackets()) {
					if (packet instanceof BatteryVoltage) {
						int fragmentId = packetGroup.getFragmentId(packet);
						BatteryVoltage batteryVoltagePacket = (BatteryVoltage) packet;
						float batteryVoltage = batteryVoltagePacket.getBatteryVoltage();
						float compensated = BatteryUtil.getOutbackCompensatedBatteryVoltage(batteryVoltage, temperatureCelsius);
						Long dateMillis = packetGroup.getDateMillis(packet);
						if (dateMillis == null) {
							dateMillis = packetGroup.getDateMillis();
						}
						r.add(new DataNode<>(compensated, batteryVoltagePacket, dateMillis, packetGroup.getSourceId(packet), fragmentId));
					}
				}
			}
			return r;
		}
		@GraphQLQuery
		public @NotNull List<@NotNull SimpleNode<Float>> batteryVoltageAverage() {
			List<SimpleNode<Float>> r = new ArrayList<>();
			for (FragmentedPacketGroup packetGroup : sortedPackets) {
				float sum = 0;
				int count = 0;
				for (Packet packet : packetGroup.getPackets()) {
					if (packet instanceof BatteryVoltage) {
						BatteryVoltage batteryVoltagePacket = (BatteryVoltage) packet;
						float batteryVoltage = batteryVoltagePacket.getBatteryVoltage();
						sum += batteryVoltage;
						count++;
					}
				}
				if (count > 0) {
					r.add(new SimpleNode<>(sum / count, packetGroup.getDateMillis()));
				}
			}
			return r;
		}
	}
	public static class SolarThingEventQuery {
		private final PacketGetter packetGetter;

		public SolarThingEventQuery(PacketGetter packetGetter) {
			this.packetGetter = packetGetter;
		}

		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<FXACModeChangePacket>> fxACModeChange() {
			return packetGetter.getPackets(FXACModeChangePacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<FXOperationalModeChangePacket>> fxOperationalModeChange() {
			return packetGetter.getPackets(FXOperationalModeChangePacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<FXAuxStateChangePacket>> fxAuxStateChange() {
			return packetGetter.getPackets(FXAuxStateChangePacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<MXAuxModeChangePacket>> mxAuxModeChange() {
			return packetGetter.getPackets(MXAuxModeChangePacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<MXRawDayEndPacket>> mxRawDayEnd() {
			return packetGetter.getPackets(MXRawDayEndPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<MXChargerModeChangePacket>> mxChargerModeChange() {
			return packetGetter.getPackets(MXChargerModeChangePacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<SuccessMateCommandPacket>> mateCommand() {
			return packetGetter.getPackets(SuccessMateCommandPacket.class);
		}
	}

}
