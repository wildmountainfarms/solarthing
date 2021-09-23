package me.retrodaredevil.solarthing.type.cache.packets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.type.cache.packets.data.ChargeControllerAccumulationDataCache;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataCacheTest {

	@Test
	void test() throws JsonProcessingException {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		Identifier identifier = new OutbackIdentifier(3);

		ChargeControllerAccumulationDataCache v1 = new ChargeControllerAccumulationDataCache(
				identifier,
				5.5f,
				3100L, 7900L,
				8.5f,
				1000L
		);
		ChargeControllerAccumulationDataCache v2 = new ChargeControllerAccumulationDataCache(
				identifier,
				6.5f,
				7900L, 13100L, // even thought this period starts at 8000, we have the last packet's dateMillis from the previous period
				0.0f,
				null
		);

		ChargeControllerAccumulationDataCache actualResult = v1.combine(v2);
		String resultJson = mapper.writeValueAsString(actualResult);
//		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualResult));
		ChargeControllerAccumulationDataCache parsedResult = mapper.readValue(resultJson, ChargeControllerAccumulationDataCache.class);

		for (ChargeControllerAccumulationDataCache result : new ChargeControllerAccumulationDataCache[] { actualResult, parsedResult }) {
			assertEquals(12.0f, result.getGenerationKWH());
			assertEquals(3100L, result.getFirstDateMillis());
			assertEquals(13100L, result.getLastDateMillis());
			assertEquals(8.5f, result.getUnknownGenerationKWH());
			assertEquals(1000L, result.getUnknownStartDateMillis());
		}
	}

}
