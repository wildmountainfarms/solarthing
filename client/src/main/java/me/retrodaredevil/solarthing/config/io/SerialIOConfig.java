package me.retrodaredevil.solarthing.config.io;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.JSerialIOBundle;
import me.retrodaredevil.io.serial.SerialConfig;

@JsonTypeName("serial")
public class SerialIOConfig implements IOConfig {
	public static final String DEFAULT_SERIAL_CONFIG_KEY = "defaultSerialConfig";
	@JsonProperty("port")
	private String port;
	@JacksonInject(DEFAULT_SERIAL_CONFIG_KEY)
	@JsonProperty("serial_config") // optional JSON override to provide custom configuration
	private SerialConfig serialConfig;

	@Override
	public IOBundle createIOBundle() throws Exception {
		return JSerialIOBundle.createPort(port, serialConfig);
	}
}
