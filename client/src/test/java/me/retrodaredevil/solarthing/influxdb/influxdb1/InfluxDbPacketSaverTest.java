package me.retrodaredevil.solarthing.influxdb.influxdb1;

import me.retrodaredevil.solarthing.influxdb.PointUtil;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;


@NullMarked
class InfluxDbPacketSaverTest {

	@Test
	void test(){
		OutbackData data = () -> 0;
		System.out.println(PointUtil.getTagKeys(data.getClass())); // TODO maybe move this class to correct place
	}
}
