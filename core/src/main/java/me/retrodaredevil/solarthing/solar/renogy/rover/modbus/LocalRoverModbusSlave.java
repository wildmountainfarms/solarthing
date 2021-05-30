package me.retrodaredevil.solarthing.solar.renogy.rover.modbus;

import me.retrodaredevil.io.modbus.FunctionCode;
import me.retrodaredevil.io.modbus.ModbusMessage;
import me.retrodaredevil.io.modbus.ModbusMessages;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.handling.MessageHandler;
import me.retrodaredevil.io.modbus.handling.ReadHoldingRegisters;
import me.retrodaredevil.io.modbus.handling.WriteMultipleRegisters;
import me.retrodaredevil.io.modbus.parsing.DefaultMessageParser;
import me.retrodaredevil.io.modbus.parsing.MessageParseException;
import me.retrodaredevil.io.modbus.parsing.MessageParser;
import me.retrodaredevil.solarthing.solar.renogy.rover.DummyRoverReadWrite;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;

import static java.util.Objects.requireNonNull;
import static me.retrodaredevil.io.modbus.ModbusMessages.convert8BitArray;
import static me.retrodaredevil.io.modbus.ModbusMessages.get16BitDataFrom8BitArray;

/**
 * Represents a {@link ModbusSlave} tightly coupled to {@link RoverModbusSlaveRead}.
 * Note that this will not work with a ModbusCacheSlave (present in program module).
 */
public class LocalRoverModbusSlave implements ModbusSlave {
	private static final MessageParser PARSER = new DefaultMessageParser();

	private final DummyRoverReadWrite rover;

	public LocalRoverModbusSlave(RoverReadTable rover) {
		this.rover = new DummyRoverReadWrite(
				rover,
				(fieldName, previousValue, newValue) -> System.out.println(fieldName + " changed from " + previousValue + " to " + newValue)
		);
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
			return handleRead(read);
		} else if (messageHandler instanceof WriteMultipleRegisters) {

		}
		throw new UnsupportedOperationException("unsupported messageHandler: " + messageHandler);
	}
	private ModbusMessage handleRead(ReadHoldingRegisters read) {
		if (read == RoverModbusSlaveRead.MAX_VOLTAGE_AND_CHARGING) {
			int maxVoltageValue = rover.getMaxVoltageValue();
			int ratedChargingCurrentValue = rover.getRatedChargingCurrentValue();
			return read.createResponse(new int[] { maxVoltageValue << 8 | ratedChargingCurrentValue });
		} else if (read == RoverModbusSlaveRead.DISCHARGING_AND_PRODUCT_TYPE) {
			int ratedDischargingCurrent = rover.getRatedDischargingCurrentValue();
			int productTypeValue = rover.getProductTypeValue();
			return read.createResponse(new int[] { ratedDischargingCurrent << 8 | productTypeValue });
		} else if (read == RoverModbusSlaveRead.PRODUCT_MODEL) {
			byte[] productModel8Bit = rover.getProductModelValue();
			return read.createResponse(get16BitDataFrom8BitArray(convert8BitArray(productModel8Bit)));
		} else if (read == RoverModbusSlaveRead.SOFTWARE_VERSION) {
			int softwareVersionValue = rover.getSoftwareVersionValue();
			return read.createResponse(new int[] { softwareVersionValue >> 16, softwareVersionValue & 0xFFFF });
		} else if (read == RoverModbusSlaveRead.HARDWARE_VERSION) {
			int hardwareVersionValue = rover.getHardwareVersionValue();
			return read.createResponse(new int[] { hardwareVersionValue >> 16, hardwareVersionValue & 0xFFFF });
		} else if (read == RoverModbusSlaveRead.PRODUCT_SERIAL) {
			int productSerialNumber = rover.getProductSerialNumber();
			return read.createResponse(new int[] { productSerialNumber >> 16, productSerialNumber & 0xFFFF });
		} else if (read == RoverModbusSlaveRead.ADDRESS) {
			int address = rover.getControllerDeviceAddress();
			return read.createResponse(new int[] { address });
		} else if (read == RoverModbusSlaveRead.PROTOCOL_VERSION) {
			Integer protocolVersion = rover.getProtocolVersionValue();
			requireNonNull(protocolVersion, "If this is called, our RoverReadTable should support this, which is does not currently. rover: " + rover);
			return read.createResponse(new int[] { protocolVersion >> 16, protocolVersion & 0xFFFF });
		} else if (read == RoverModbusSlaveRead.ID_CODE) {
			Integer idCode = rover.getUniqueIdentificationCode();
			requireNonNull(idCode, "If this is called, our RoverReadTable should support this, which is does not currently. rover: " + rover);
			return read.createResponse(new int[] { idCode >> 16, idCode & 0xFFFF });
		} else if (read == RoverModbusSlaveRead.SOC) {
			int soc = rover.getBatteryCapacitySOC();
			return read.createResponse(new int[] { soc });
		} else if (read == RoverModbusSlaveRead.BATTERY_VOLTAGE) {
			int batteryVoltageRaw = Math.round(rover.getBatteryVoltage() * 10.0f);
			return read.createResponse(new int[] { batteryVoltageRaw });
		} else if (read == RoverModbusSlaveRead.CHARGING_CURRENT) {
			int chargingCurrentRaw = Math.round(rover.getChargingCurrent() * 100.0f);
			return read.createResponse(new int[] { chargingCurrentRaw });
		} else if (read == RoverModbusSlaveRead.CONTROLLER_BATTERY_TEMPERATURE) {
			int controllerTemperatureRaw = rover.getControllerTemperatureRaw();
			int batteryTemperatureRaw = rover.getBatteryTemperatureRaw();
			return read.createResponse(new int[] { controllerTemperatureRaw << 8 | batteryTemperatureRaw });
		} else if (read == RoverModbusSlaveRead.LOAD_VOLTAGE) {
			int loadVoltageRaw = Math.round(rover.getLoadVoltageRaw() * 10.0f);
			return read.createResponse(new int[] { loadVoltageRaw });
		} else if (read == RoverModbusSlaveRead.LOAD_CURRENT) {
			int loadCurrentRaw = Math.round(rover.getLoadCurrentRaw() * 10.0f);
			return read.createResponse(new int[] { loadCurrentRaw });
		} else if (read == RoverModbusSlaveRead.LOAD_POWER) {
			int loadPower = rover.getLoadPowerRaw();
			return read.createResponse(new int[] { loadPower });
		} else if (read == RoverModbusSlaveRead.PV_VOLTAGE) {
			int pvVoltageRaw = Math.round(rover.getPVVoltage() * 10.0f);
			return read.createResponse(new int[] { pvVoltageRaw });
		}
		/*
		else if (read == RoverModbusSlaveRead.LOAD_VOLTAGEX) {
			int loadVoltageRaw = Math.round(rover.getLoadVoltageRaw() * 10.0f);
			return read.createResponse(new int[] { loadVoltageRaw });
		} else if (read == RoverModbusSlaveRead.LOAD_VOLTAGEX) {
			int loadVoltageRaw = Math.round(rover.getLoadVoltageRaw() * 10.0f);
			return read.createResponse(new int[] { loadVoltageRaw });
		} else if (read == RoverModbusSlaveRead.LOAD_VOLTAGEX) {
			int loadVoltageRaw = Math.round(rover.getLoadVoltageRaw() * 10.0f);
			return read.createResponse(new int[] { loadVoltageRaw });
		} else if (read == RoverModbusSlaveRead.LOAD_VOLTAGEX) {
			int loadVoltageRaw = Math.round(rover.getLoadVoltageRaw() * 10.0f);
			return read.createResponse(new int[] { loadVoltageRaw });
		} else if (read == RoverModbusSlaveRead.LOAD_VOLTAGEX) {
			int loadVoltageRaw = Math.round(rover.getLoadVoltageRaw() * 10.0f);
			return read.createResponse(new int[] { loadVoltageRaw });
		} else if (read == RoverModbusSlaveRead.LOAD_VOLTAGEX) {
			int loadVoltageRaw = Math.round(rover.getLoadVoltageRaw() * 10.0f);
			return read.createResponse(new int[] { loadVoltageRaw });
		} else if (read == RoverModbusSlaveRead.LOAD_VOLTAGEX) {
			int loadVoltageRaw = Math.round(rover.getLoadVoltageRaw() * 10.0f);
			return read.createResponse(new int[] { loadVoltageRaw });
		}
		 */

		return ModbusMessages.createExceptionMessage(FunctionCode.READ_HOLDING_REGISTERS, RoverModbusSlaveRead.READ_EXCEPTION_UNSUPPORTED_REGISTER);
	}
}
