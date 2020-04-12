package me.retrodaredevil.solarthing.graphql;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.graphql.packets.PacketNode;
import me.retrodaredevil.solarthing.graphql.packets.PacketUtil;
import me.retrodaredevil.solarthing.misc.device.CpuTemperaturePacket;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.extra.DailyMXPacket;

import javax.validation.constraints.NotNull;
import java.util.List;

public class SolarThingGraphQLService {
	private List<FragmentedPacketGroup> queryPackets(long from, long to) {
		throw new UnsupportedOperationException();
	}

	@GraphQLQuery
	public QueryFromTo queryAll(@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to){
		return new QueryFromTo(from, to);
	}
	public class QueryFromTo {
		private final List<FragmentedPacketGroup> packets;

		public QueryFromTo(long from, long to) {
			packets = queryPackets(from, to);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<BatteryVoltage>> batteryVoltage() {
			return PacketUtil.convertPackets(packets, BatteryVoltage.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<FXStatusPacket>> fxStatus() {
			return PacketUtil.convertPackets(packets, FXStatusPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<MXStatusPacket>> mxStatus() {
			return PacketUtil.convertPackets(packets, MXStatusPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<DailyMXPacket>> mxDaily() {
			return PacketUtil.convertPackets(packets, DailyMXPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<DailyFXPacket>> fxDaily() {
			return PacketUtil.convertPackets(packets, DailyFXPacket.class);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull PacketNode<CpuTemperaturePacket>> cpuTemperature() {
			return PacketUtil.convertPackets(packets, CpuTemperaturePacket.class);
		}
		//	public List<InstancePacketGroup> queryInstance(long from, long to, @NotNull String sourceId, @NotNull Integer fragmentId) { throw new UnsupportedOperationException(); }
		public List<InstancePacketGroup> queryUnsorted(String sourceId) { throw new UnsupportedOperationException(); }
		public List<FragmentedPacketGroup> querySorted(String sourceId) { throw new UnsupportedOperationException(); }

	}
}
