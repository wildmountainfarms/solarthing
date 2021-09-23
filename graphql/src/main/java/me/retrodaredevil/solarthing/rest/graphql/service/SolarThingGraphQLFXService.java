package me.retrodaredevil.solarthing.rest.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.type.closed.meta.MetaDatabase;
import me.retrodaredevil.solarthing.type.closed.meta.TargetedMetaPacket;
import me.retrodaredevil.solarthing.type.closed.meta.TargetedMetaPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentUtil;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.rest.exceptions.UnexpectedResponseException;
import me.retrodaredevil.solarthing.rest.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.DataNode;
import me.retrodaredevil.solarthing.solar.common.BatteryTemperature;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingSettings;
import me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingStateHandler;
import me.retrodaredevil.solarthing.solar.outback.fx.charge.ImmutableFXChargingPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.meta.FXChargingSettingsPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.meta.FXChargingTemperatureAdjustPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SolarThingGraphQLFXService {
	private final SimpleQueryHandler simpleQueryHandler;

	public SolarThingGraphQLFXService(SimpleQueryHandler simpleQueryHandler) {
		this.simpleQueryHandler = simpleQueryHandler;
	}
	@GraphQLQuery(description = "Gives the timer values for the master FX of a single fragment over a time range")
	public List<DataNode<FXChargingPacket>> queryFXCharging(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "fragmentId") int fragmentId){
		MetaDatabase metaDatabase = simpleQueryHandler.queryMeta();
		FXChargingSettingsPacket fxChargingSettingsPacket = null;
		FXChargingTemperatureAdjustPacket fxChargingTemperatureAdjustPacket = null;
		for (TargetedMetaPacket targetedMetaPacket : metaDatabase.getMeta(to, fragmentId)) {
			if (targetedMetaPacket.getPacketType() == TargetedMetaPacketType.FX_CHARGING_SETTINGS) {
				fxChargingSettingsPacket = (FXChargingSettingsPacket) targetedMetaPacket;
			} else if (targetedMetaPacket.getPacketType() == TargetedMetaPacketType.FX_CHARGING_TEMPERATURE_ADJUST) {
				fxChargingTemperatureAdjustPacket = (FXChargingTemperatureAdjustPacket) targetedMetaPacket;
			}
		}
		if (fxChargingSettingsPacket == null) {
			throw new UnexpectedResponseException("Could not find FX Charging settings in meta!");
		}

		long startTime = from - 3 * 60 * 60 * 1000; // 3 hours back
		List<? extends InstancePacketGroup> packets = simpleQueryHandler.queryStatus(startTime, to, null);

		// We make masterIdIgnoreDistance null because we will only be using fragmentId as the master fragment ID
		Map<String, List<FragmentedPacketGroup>> map = PacketGroups.sortPackets( // separate based on source ID
				packets, simpleQueryHandler.getDefaultInstanceOptions(), SolarThingConstants.STANDARD_MAX_TIME_DISTANCE.toMillis(), null,
				FragmentUtil.createPriorityComparator(fragmentId) // make fragmentId be the master ID
		);
		List<FragmentedPacketGroup> sortedPackets = null;
		for (List<FragmentedPacketGroup> fragmentedPacketGroups : map.values()) {
			if (fragmentedPacketGroups.get(0).hasFragmentId(fragmentId)) {
				sortedPackets = fragmentedPacketGroups;
				break;
			}
		}
		if (sortedPackets == null) {
			throw new UnexpectedResponseException("Could not find fragment ID: " + fragmentId);
		}
		FXChargingSettings settings = fxChargingSettingsPacket.getFXChargingSettings();
		FXChargingStateHandler stateHandler = new FXChargingStateHandler(settings);
		Long lastUpdate = null;

		List<DataNode<FXChargingPacket>> r = new ArrayList<>();
		for (FragmentedPacketGroup packetGroup : sortedPackets) {
			List<FXStatusPacket> fxPackets = new ArrayList<>();
			Integer temperature = null;
			for (Packet packet : packetGroup.getPackets()) {
				if (packet instanceof FXStatusPacket && packetGroup.getFragmentId(packet) == fragmentId) {
					fxPackets.add((FXStatusPacket) packet);
				} else if (packet instanceof BatteryTemperature) {
					temperature = Math.round(((BatteryTemperature) packet).getBatteryTemperatureCelsius().floatValue());
				}
			}
			if (fxPackets.isEmpty()) {
				continue;
			}
			if (temperature == null) {
				continue; // we need temperature data for accurate results // TODO add option for systems that don't use temperature compensation
			}
			temperature += fxChargingTemperatureAdjustPacket == null ? 0 : fxChargingTemperatureAdjustPacket.getTemperatureAdjustCelsius();
			FXStatusPacket fx = OutbackUtil.getMasterFX(fxPackets);
			if (fx == null) {
				continue;
			}
			final long delta;
			if (lastUpdate == null) {
				delta = 1000;
			} else {
				delta = packetGroup.getDateMillis() - lastUpdate;
			}
			lastUpdate = packetGroup.getDateMillis();
			stateHandler.update(delta, fx, temperature);
			FXChargingPacket fxChargingPacket = new ImmutableFXChargingPacket(
					fx.getIdentifier(), stateHandler.getMode(),
					stateHandler.getRemainingAbsorbTimeMillis(), stateHandler.getRemainingFloatTimeMillis(), stateHandler.getRemainingEqualizeTimeMillis(),
					settings.getAbsorbTimeMillis(), settings.getFloatTimeMillis(), settings.getEqualizeTimeMillis()
			);
			r.add(new DataNode<>(fxChargingPacket, fx, packetGroup.getDateMillis(), packetGroup.getSourceId(fx), fragmentId));
		}
		if (r.isEmpty() && !sortedPackets.isEmpty()) {
			throw new UnexpectedResponseException("There must have been no FX packets or no rover packets!");
		}
		return r;
	}
}
