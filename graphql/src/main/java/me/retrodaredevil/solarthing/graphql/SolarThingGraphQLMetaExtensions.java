package me.retrodaredevil.solarthing.graphql;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.graphql.packets.PacketNode;
import me.retrodaredevil.solarthing.graphql.packets.SimplePacketNode;
import me.retrodaredevil.solarthing.meta.DeviceInfoPacket;
import me.retrodaredevil.solarthing.meta.MetaDatabase;
import me.retrodaredevil.solarthing.meta.BasicMetaPacket;
import me.retrodaredevil.solarthing.meta.TargetedMetaPacket;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiable;
import me.retrodaredevil.solarthing.misc.common.meta.DataMetaPacket;
import me.retrodaredevil.solarthing.misc.weather.TemperaturePacket;

import java.util.function.Supplier;

public class SolarThingGraphQLMetaExtensions {
	private final Supplier<MetaDatabase> metaDatabaseSupplier;

	public SolarThingGraphQLMetaExtensions(Supplier<MetaDatabase> metaDatabaseSupplier) {
		this.metaDatabaseSupplier = metaDatabaseSupplier;
	}

	@GraphQLQuery(name = "meta")
	public DataMetaPacket getMetaTemperaturePacket(@GraphQLContext PacketNode<TemperaturePacket> packetNode){
		return getMeta(packetNode);
	}
	private DataMetaPacket getMeta(PacketNode<? extends DataIdentifiable> packetNode) {
		int fragmentId = packetNode.getFragmentId();
		int dataId = packetNode.getPacket().getDataId();
		MetaDatabase metaDatabase = metaDatabaseSupplier.get();
		for (TargetedMetaPacket targetedMetaPacket : metaDatabase.getMeta(packetNode.getDateMillis(), fragmentId)) {
			if (targetedMetaPacket instanceof DataMetaPacket) {
				DataMetaPacket dataMetaPacket = (DataMetaPacket) targetedMetaPacket;
				if (dataMetaPacket.getDataId() == dataId) {
					return dataMetaPacket;
				}
			}
		}
		return null;
	}
	@GraphQLQuery(name = "deviceInfo")
	public DeviceInfoPacket getDeviceInfo(@GraphQLContext SimplePacketNode packetNode){
		int fragmentId = packetNode.getFragmentId();
		MetaDatabase metaDatabase = metaDatabaseSupplier.get();
		for (TargetedMetaPacket targetedMetaPacket : metaDatabase.getMeta(packetNode.getDateMillis(), fragmentId)) {
			if (targetedMetaPacket instanceof DeviceInfoPacket) {
				return (DeviceInfoPacket) targetedMetaPacket;
			}
		}
		return null;
	}
}
