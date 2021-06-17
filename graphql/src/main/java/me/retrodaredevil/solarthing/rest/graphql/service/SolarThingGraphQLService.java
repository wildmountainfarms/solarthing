package me.retrodaredevil.solarthing.rest.graphql.service;

import io.leangen.graphql.annotations.*;
import io.leangen.graphql.annotations.types.GraphQLType;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.rest.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.rest.graphql.packets.*;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.DataNode;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.SimpleNode;
import me.retrodaredevil.solarthing.meta.*;
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
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.solar.outback.command.packets.SuccessMateCommandPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXACModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXAuxStateChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXOperationalModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.meta.FXChargingTemperatureAdjustPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXAuxModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXChargerModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXRawDayEndPacket;
import me.retrodaredevil.solarthing.solar.pzem.PzemShuntStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.retrodaredevil.solarthing.rest.graphql.service.SchemaConstants.*;

@GraphQLType
public class SolarThingGraphQLService {

	private final SimpleQueryHandler simpleQueryHandler;

	public SolarThingGraphQLService(SimpleQueryHandler simpleQueryHandler) {
		this.simpleQueryHandler = simpleQueryHandler;
	}


	@GraphQLQuery(description = "Query status packets in the specified time range.")
	public SolarThingStatusQuery queryStatus(
			@GraphQLArgument(name = "from", description = DESCRIPTION_FROM) long from, @GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId){
		List<? extends InstancePacketGroup> packets = simpleQueryHandler.queryStatus(from, to, sourceId);
		return new SolarThingStatusQuery(new BasicPacketGetter(packets, PacketFilter.KEEP_ALL), simpleQueryHandler.sortPackets(packets, sourceId), simpleQueryHandler);
	}
	@GraphQLQuery(description = "Query the latest collection of status packets on or before the 'to' timestamp.")
	public SolarThingStatusQuery queryStatusLast(
			@GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId,
			@GraphQLArgument(name = "reversed", defaultValue = "false", description = "If set to true, the returned list will be reversed. Useful to set to true if you want the very latest packet to be first.") boolean reversed){
		List<? extends InstancePacketGroup> packets = simpleQueryHandler.queryStatus(to - SolarThingConstants.LATEST_PACKETS_DURATION.toMillis(), to, sourceId);
		List<InstancePacketGroup> lastPackets = new ArrayList<>();
		for(List<InstancePacketGroup> packetGroups : PacketGroups.mapFragments(packets).values()) {
			lastPackets.add(packetGroups.get(packetGroups.size() - 1));
		}
		return new SolarThingStatusQuery(new ReversedPacketGetter(new LastPacketGetter(packets, PacketFilter.KEEP_ALL), reversed), simpleQueryHandler.sortPackets(lastPackets, sourceId), simpleQueryHandler);
	}
	@GraphQLQuery
	public SolarThingEventQuery queryEvent(
			@GraphQLArgument(name = "from", description = DESCRIPTION_FROM) long from, @GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "sourceId", description = DESCRIPTION_OPTIONAL_SOURCE) @Nullable String sourceId,
			@GraphQLArgument(name = "includeUnknownChangePackets", defaultValue = "false", description = DESCRIPTION_INCLUDE_UNKNOWN_CHANGE) boolean includeUnknownChangePackets
	) {
		return new SolarThingEventQuery(new BasicPacketGetter(simpleQueryHandler.queryEvent(from, to, sourceId), new UnknownChangePacketsFilter(includeUnknownChangePackets)));
	}
	@GraphQLQuery(description = "Queries events in the specified time range while only including the specified identifier in the specified fragment")
	public SolarThingEventQuery queryEventIdentifier(
			@GraphQLArgument(name = "from", description = DESCRIPTION_FROM) long from, @GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "fragmentId", description = DESCRIPTION_FRAGMENT_ID) int fragmentId,
			@GraphQLArgument(name = "identifier") @NotNull String identifierRepresentation,
			@GraphQLArgument(name = "includeUnknownChangePackets", defaultValue = "false", description = DESCRIPTION_INCLUDE_UNKNOWN_CHANGE) boolean includeUnknownChangePackets,
			@GraphQLArgument(name = "acceptSupplementary", defaultValue = "true") boolean acceptSupplementary
	) {
		return new SolarThingEventQuery(new BasicPacketGetter(
				simpleQueryHandler.queryEvent(from, to, null), // null source ID because each fragment ID is unique, even over multiple sources
				new PacketFilterMultiplexer(Arrays.asList(new FragmentFilter(fragmentId), new IdentifierFilter(identifierRepresentation, acceptSupplementary), new UnknownChangePacketsFilter(includeUnknownChangePackets)))
		));
	}
	@GraphQLQuery(description = "Queries events in the specified time range while only including the specified fragment")
	public SolarThingEventQuery queryEventFragment(
			@GraphQLArgument(name = "from", description = DESCRIPTION_FROM) long from, @GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "fragmentId", description = DESCRIPTION_FRAGMENT_ID) int fragmentId,
			@GraphQLArgument(name = "includeUnknownChangePackets", defaultValue = "false", description = DESCRIPTION_INCLUDE_UNKNOWN_CHANGE) boolean includeUnknownChangePackets
	) {
		return new SolarThingEventQuery(new BasicPacketGetter(
				simpleQueryHandler.queryEvent(from, to, null),
				new PacketFilterMultiplexer(Arrays.asList(new FragmentFilter(fragmentId), new UnknownChangePacketsFilter(includeUnknownChangePackets)))
		));
	}
	public static class SolarThingStatusQuery {
		private final PacketGetter packetGetter;
		private final List<? extends FragmentedPacketGroup> sortedPackets;
		private final SimpleQueryHandler simpleQueryHandler;

		public SolarThingStatusQuery(PacketGetter packetGetter, List<? extends FragmentedPacketGroup> sortedPackets, SimpleQueryHandler simpleQueryHandler) {
			this.packetGetter = packetGetter;
			this.sortedPackets = sortedPackets;
			this.simpleQueryHandler = simpleQueryHandler;
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
		public @NotNull List<@NotNull PacketNode<PzemShuntStatusPacket>> pzemShuntStatus() {
			return packetGetter.getPackets(PzemShuntStatusPacket.class);
		}


		@GraphQLQuery
		public @NotNull List<@NotNull DataNode<Float>> batteryVoltageTemperatureCompensated() {
			MetaDatabase metaDatabase = simpleQueryHandler.queryMeta();
			FXChargingTemperatureAdjustPacket fxChargingTemperatureAdjustPacket = null;
			// Depending on the date, there could be different configurations. So, this just gets the date of the last packet
			long currentConfigurationDateMillis = sortedPackets.get(sortedPackets.size() - 1).getDateMillis();
			for (BasicMetaPacket basicMetaPacket : metaDatabase.getMeta(currentConfigurationDateMillis)) {
				if (basicMetaPacket instanceof TargetMetaPacket) {
					TargetMetaPacket targetMetaPacket = (TargetMetaPacket) basicMetaPacket;
					for (TargetedMetaPacket targetedMetaPacket : targetMetaPacket.getPackets()) {
						if (targetedMetaPacket.getPacketType() == TargetedMetaPacketType.FX_CHARGING_TEMPERATURE_ADJUST) {
							fxChargingTemperatureAdjustPacket = (FXChargingTemperatureAdjustPacket) targetedMetaPacket;
							break; // just use the first one, we don't care which fragment it belongs to
						}
					}
				}
			}

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
				int temperatureCelsius = rover.getBatteryTemperatureCelsius() + (fxChargingTemperatureAdjustPacket == null ? 0 : fxChargingTemperatureAdjustPacket.getTemperatureAdjustCelsius());
				for (Packet packet : packetGroup.getPackets()) {
					if (packet instanceof BatteryVoltage) {
						int fragmentId = packetGroup.getFragmentId(packet);
						BatteryVoltage batteryVoltagePacket = (BatteryVoltage) packet;
						float batteryVoltage = batteryVoltagePacket.getBatteryVoltage();
						float compensated = BatteryUtil.getOutbackCompensatedBatteryVoltage(batteryVoltage, temperatureCelsius);
						long dateMillis = packetGroup.getDateMillisOrKnown(packet);
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
		public @NotNull List<@NotNull PacketNode<FXOperationalModeChangePacket>> fxOperationalModeChange(
				@GraphQLArgument(name = "include") List<@NotNull OperationalMode> include,
				@GraphQLArgument(name = "exclude") List<@NotNull OperationalMode> exclude
		) {
			List<@NotNull PacketNode<FXOperationalModeChangePacket>> r = packetGetter.getPackets(FXOperationalModeChangePacket.class);
			if (include != null) {
				r.removeIf(packetNode -> !include.contains(packetNode.getPacket().getOperationalMode()));
			} else if (exclude != null) {
				r.removeIf(packetNode -> exclude.contains(packetNode.getPacket().getOperationalMode()));
			}
			return r;
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
		public @NotNull List<@NotNull PacketNode<SuccessMateCommandPacket>> mateCommand(
				@GraphQLArgument(name = "include") List<@NotNull MateCommand> include,
				@GraphQLArgument(name = "exclude") List<@NotNull MateCommand> exclude
		) {
			List<@NotNull PacketNode<SuccessMateCommandPacket>> r = packetGetter.getPackets(SuccessMateCommandPacket.class);
			if (include != null) {
				r.removeIf(packetNode -> !include.contains(packetNode.getPacket().getCommand()));
			} else if (exclude != null) {
				r.removeIf(packetNode -> exclude.contains(packetNode.getPacket().getCommand()));
			}
			return r;
		}
	}

}
