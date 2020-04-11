package me.retrodaredevil.solarthing.graphql;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPackets;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPackets;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolarThingGraphQLService {
	@GraphQLQuery
	public MXStatusPacket lastMXStatus() {
		try {
			return MXStatusPackets.createFromChars("\nD,00,10,99,999,888,07,09,000,04,282,000,000,122\r".toCharArray(), IgnoreCheckSum.IGNORE_AND_USE_CALCULATED);
		} catch (CheckSumException | ParsePacketAsciiDecimalDigitException e) {
			throw new RuntimeException(e);
		}
	}
	@GraphQLQuery
	public FXStatusPacket lastFXStatus() {
		try {
			return FXStatusPackets.createFromChars("\n1,07,00,00,000,125,00,03,000,00,282,000,000,999\r".toCharArray(), IgnoreCheckSum.IGNORE_AND_USE_CALCULATED);
		} catch (ParsePacketAsciiDecimalDigitException | CheckSumException e) {
			throw new RuntimeException(e);
		}
	}
	public List<InstancePacketGroup> queryInstance(String sourceId, Integer fragmentId) { throw new UnsupportedOperationException(); }
	public List<InstancePacketGroup> queryUnsorted(String sourceId) { throw new UnsupportedOperationException(); }
	public List<InstancePacketGroup> querySorted(String sourceId) { throw new UnsupportedOperationException(); }

	@GraphQLQuery
	public List<TestQuery> submissions(@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to){
		List<TestQuery> r = new ArrayList<>();
		for(long start = from; start < to; start += 100000){
			TestQuery testQuery = new TestQuery();
			testQuery.submitTime = start;
			testQuery.running = (float) (6.6 * Math.random());
			testQuery.name = "Josh";

			TestQuery test2 = new TestQuery();
			test2.submitTime = start + 50000;
			test2.running = (float) (6.6 * Math.random());
			test2.name = "Josh2";

			r.add(testQuery);
			r.add(test2);
		}
		return r;
	}
	public static class TestQuery {
		public long submitTime;
		public float idle;
		public float running;
		public float completed;
		public String name;
	}
}
