package me.retrodaredevil.solarthing.program.check;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.modbus.ModbusMessages;
import me.retrodaredevil.io.modbus.handling.ErrorCodeException;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModbusSerializationCatcherTest {

	@Test
	void test() throws JsonProcessingException {
		ObjectMapper modbusCatchingMapper = JacksonUtil.defaultMapper();
		modbusCatchingMapper.registerModule(ModbusSerializationCatcher.createModule(TestObject.class));
		assertEquals("{\"test1\":5,\"test2\":5,\"test3\":\"Modbus Exception: 2\"}", modbusCatchingMapper.writeValueAsString(new TestObject()));
	}

	private static class TestObject {
		@JsonProperty
		private int test1 = 5;
		@JsonProperty
		private int test2 = 5;

		@JsonProperty
		private int getTest3() {
			throw new ErrorCodeException(ModbusMessages.createMessage(131, new int[0]), 3, 2);
		}
	}
}
