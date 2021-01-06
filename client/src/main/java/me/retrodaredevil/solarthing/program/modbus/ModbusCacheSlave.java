package me.retrodaredevil.solarthing.program.modbus;

import me.retrodaredevil.io.modbus.ModbusMessage;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.MessageHandler;
import me.retrodaredevil.io.modbus.handling.ReadHoldingRegisters;
import me.retrodaredevil.io.modbus.parsing.DefaultMessageParser;
import me.retrodaredevil.io.modbus.parsing.MessageParseException;
import me.retrodaredevil.io.modbus.parsing.MessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ModbusCacheSlave implements ModbusSlave {
	private static final MessageParser PARSER = new DefaultMessageParser();
	private static final Logger LOGGER = LoggerFactory.getLogger(ModbusCacheSlave.class);

	private final ModbusSlave modbusSlave;
	private final Map<Integer, Integer> cache = new HashMap<>();

	public ModbusCacheSlave(ModbusSlave modbusSlave) {
		this.modbusSlave = modbusSlave;
	}

	public void cache(int startRegister, int numberOfRegisters) {
		int[] registers = modbusSlave.sendRequestMessage(new ReadHoldingRegisters(startRegister, numberOfRegisters));
		for (int i = 0; i < registers.length; i++) {
			int register = startRegister + i;
			int value = registers[i];
			cache.put(register, value);
		}
	}
	public void cacheRangeInclusive(int startRegister, int endRegister) {
		cache(startRegister, endRegister - startRegister + 1);
	}

	@Override
	public ModbusMessage sendRequestMessage(ModbusMessage message) {
		final MessageHandler<?> messageHandler;
		try {
			messageHandler = PARSER.parseRequestMessage(message);
		} catch (MessageParseException e) {
			throw new RuntimeException("Got error while parsing message. This should never happen because message should be from a trusted (local) source.", e);
		}
		if (messageHandler instanceof ReadHoldingRegisters) {
			ReadHoldingRegisters read = (ReadHoldingRegisters) messageHandler;
			int[] values = new int[read.getNumberOfRegisters()];
			for (int i = 0; i < values.length; i++) {
				int register = read.getStartingDataAddress() + i;
				Integer value = cache.get(register);
				if (value == null) {
//					LOGGER.debug("Register: " + register + " didn't have a cached value.");
					return modbusSlave.sendRequestMessage(message);
				}
				values[i] = value;
			}
			return read.createResponse(values);
		}
		LOGGER.debug("Couldn't handle message using messageHandler=" + messageHandler);
		return modbusSlave.sendRequestMessage(message);
	}
}
