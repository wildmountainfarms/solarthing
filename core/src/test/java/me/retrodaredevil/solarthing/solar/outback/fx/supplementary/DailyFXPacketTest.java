package me.retrodaredevil.solarthing.solar.outback.fx.supplementary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DailyFXPacketTest {
	@Test
	void test(){
		DailyFXPacket packet = new ImmutableDailyFXPacket(System.currentTimeMillis(), 22, 22.3f, 0, 0, 0, 0, Collections.emptySet(), 0, 0, 0, Collections.emptySet(), new OutbackIdentifier(0));
		Gson gson = new GsonBuilder().serializeNulls().create();
		String json = gson.toJson(packet);
		DailyFXPacket output = DailyFXPackets.createFromJson(JsonParser.parseString(json).getAsJsonObject());
		String json2 = gson.toJson(output);
		assertEquals(json, json2);
	}
}
