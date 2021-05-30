package me.retrodaredevil.solarthing.config.io.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.LocalRoverModbusSlave;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@JsonTypeName("rover")
public class DummyRoverModbusSlave implements DummyModbusSlave {
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final File file;

	@JsonCreator
	public DummyRoverModbusSlave(@JsonProperty("file") File file) {
		this.file = file;
	}

	@Override
	public ModbusSlave createModbusSlave() {
		final FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("The dummy file was not found!", e);
		}
		final RoverStatusPacket roverStatusPacket;
		try {
			roverStatusPacket = MAPPER.readValue(fileInputStream, RoverStatusPacket.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new LocalRoverModbusSlave(roverStatusPacket);
	}
}
