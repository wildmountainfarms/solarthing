package me.retrodaredevil.solarthing.rest.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.SimplePacketNode;
import me.retrodaredevil.solarthing.type.closed.meta.DeviceInfoPacket;
import me.retrodaredevil.solarthing.type.closed.meta.MetaDatabase;
import me.retrodaredevil.solarthing.type.closed.meta.RootMetaPacket;
import me.retrodaredevil.solarthing.type.closed.meta.TargetedMetaPacket;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiable;
import me.retrodaredevil.solarthing.misc.common.meta.DataMetaPacket;
import me.retrodaredevil.solarthing.misc.weather.TemperaturePacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;

public class SolarThingGraphQLMetaService {
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final SimpleQueryHandler simpleQueryHandler;

	public SolarThingGraphQLMetaService(SimpleQueryHandler simpleQueryHandler) {
		this.simpleQueryHandler = simpleQueryHandler;
	}
	@GraphQLQuery(name = "meta")
	public @Nullable DataMetaPacket getMetaTemperaturePacket(@GraphQLContext PacketNode<TemperaturePacket> packetNode){
		return getMeta(packetNode);
	}

	private @Nullable DataMetaPacket getMeta(PacketNode<? extends DataIdentifiable> packetNode) {
		int fragmentId = packetNode.getFragmentId();
		int dataId = packetNode.getPacket().getDataId();
		MetaDatabase metaDatabase = simpleQueryHandler.queryMeta();
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

	@GraphQLQuery(name = "fragmentDeviceInfo")
	public @Nullable DeviceInfoPacket getFragmentDeviceInfo(@GraphQLContext SimplePacketNode packetNode){
		int fragmentId = packetNode.getFragmentId();
		MetaDatabase metaDatabase = simpleQueryHandler.queryMeta();
		for (TargetedMetaPacket targetedMetaPacket : metaDatabase.getMeta(packetNode.getDateMillis(), fragmentId)) {
			if (targetedMetaPacket instanceof DeviceInfoPacket) {
				return (DeviceInfoPacket) targetedMetaPacket;
			}
		}
		return null;
	}

	/**
	 *
	 * @return
	 */
	@GraphQLQuery(name = "metaDocument")
	public @NotNull RootMetaPacket getMetaDocument() {
		// It's too bad that we use queryRawMeta() here
		VersionedPacket<RootMetaPacket> metaDatabase = simpleQueryHandler.queryRawMeta();
		RootMetaPacket root = metaDatabase.getPacket();
		final String jsonData;
		try {
			jsonData = MAPPER.writeValueAsString(root);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}

	@JsonExplicit
	public static class MetaDocumentInfo {
		private final String jsonData;
		private final RootMetaPacket rootMeta;

		public MetaDocumentInfo(String jsonData, RootMetaPacket rootMeta) {
			this.jsonData = jsonData;
			this.rootMeta = rootMeta;
		}

		public String getJsonData() {
			return jsonData;
		}

		public RootMetaPacket getRootMeta() {
			return rootMeta;
		}
	}
	public static class MetaDocumentInput {
		private final String jsonData;
	}
}
