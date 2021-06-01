package me.retrodaredevil.solarthing.program.modbus;

import me.retrodaredevil.io.modbus.ModbusMessage;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.BaseReadRegisters;
import me.retrodaredevil.io.modbus.handling.MessageHandler;
import me.retrodaredevil.io.modbus.handling.ReadHoldingRegisters;
import me.retrodaredevil.io.modbus.handling.ReadInputRegisters;
import me.retrodaredevil.io.modbus.parsing.DefaultMessageParser;
import me.retrodaredevil.io.modbus.parsing.MessageParseException;
import me.retrodaredevil.io.modbus.parsing.MessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * A {@link ModbusSlave} which can be told periodically to cache certain values from a given {@link ModbusSlave}.
 * <p>
 * The advantage to this is that bulk requests can be performed and cached, then smaller requests can use that cache
 * to avoid many unnecessary and inefficient small requests.
 */
public class ModbusCacheSlave implements ModbusSlave {
	private static final MessageParser PARSER = new DefaultMessageParser();
	private static final Logger LOGGER = LoggerFactory.getLogger(ModbusCacheSlave.class);

	private final ModbusSlave modbusSlave;
	private final Map<Integer, Integer> holdingRegisterCache = new HashMap<>();
	private final Map<Integer, Integer> inputRegisterCache = new HashMap<>();

	public ModbusCacheSlave(ModbusSlave modbusSlave) {
		this.modbusSlave = modbusSlave;
	}

	private void cache(int startRegister, int numberOfRegisters, Map<Integer, Integer> map, BiFunction<Integer, Integer, MessageHandler<int[]>> messageHandlerCreator) {
		int[] registers = modbusSlave.sendRequestMessage(messageHandlerCreator.apply(startRegister, numberOfRegisters));
		for (int i = 0; i < registers.length; i++) {
			int register = startRegister + i;
			int value = registers[i];
			map.put(register, value);
		}
	}

	public void cacheHolding(int startRegister, int numberOfRegisters) {
		cache(startRegister, numberOfRegisters, holdingRegisterCache, ReadHoldingRegisters::new);
	}
	public void cacheHoldingRangeInclusive(int startRegister, int endRegister) {
		cacheHolding(startRegister, endRegister - startRegister + 1);
	}

	public void cacheInput(int startRegister, int numberOfRegisters) {
		cache(startRegister, numberOfRegisters, inputRegisterCache, ReadInputRegisters::new);
	}
	public void cacheInputRangeInclusive(int startRegister, int endRegister) {
		cacheInput(startRegister, endRegister - startRegister + 1);
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
			return readFrom(message, read, holdingRegisterCache);
		} else if (messageHandler instanceof ReadInputRegisters) {
			ReadInputRegisters read = (ReadInputRegisters) messageHandler;
			return readFrom(message, read, inputRegisterCache);
		}
//		LOGGER.debug("Couldn't handle message using messageHandler=" + messageHandler);
		return modbusSlave.sendRequestMessage(message);
	}
	private ModbusMessage readFrom(ModbusMessage message, BaseReadRegisters read, Map<Integer, Integer> map) {
		int[] values = new int[read.getNumberOfRegisters()];
		for (int i = 0; i < values.length; i++) {
			int register = read.getStartingDataAddress() + i;
			Integer value = map.get(register);
			if (value == null) {
				if (i != 0) {
					LOGGER.debug("Register: " + register + " didn't have a cached value, however some values for this request were cached. i: " + i);
				}
				return modbusSlave.sendRequestMessage(message);
			}
			values[i] = value;
		}
		return read.createResponse(values);
	}
}
