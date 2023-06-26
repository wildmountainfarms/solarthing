package me.retrodaredevil.solarthing.config.io.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.modbus.LocalRoverModbusSlave;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@JsonTypeName("rover")
public class DummyRoverModbusSlave implements DummyModbusSlave {
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final Path file;

	@JsonCreator
	public DummyRoverModbusSlave(@JsonProperty("file") Path file) {
		this.file = file;
	}

	@Override
	public ModbusSlave createModbusSlave() {
		final InputStream fileInputStream;
		try {
			fileInputStream = Files.newInputStream(file);
		} catch (IOException e) {
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
