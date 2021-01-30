package me.retrodaredevil.solarthing.influxdb.influxdb1;

import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import org.junit.jupiter.api.Test;

import static me.retrodaredevil.solarthing.influxdb.influxdb1.InfluxDbPacketSaver.getTagKeys;

class InfluxDbPacketSaverTest {

	@Test
	void test(){
		OutbackData data = () -> 0;
		System.out.println(getTagKeys(data.getClass()));
	}
}
