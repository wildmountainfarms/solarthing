package me.retrodaredevil.solarthing.graphql;

import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPackets;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPackets;
import me.retrodaredevil.solarthing.util.CheckSumException;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.solarthing.util.ParsePacketAsciiDecimalDigitException;

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
}
