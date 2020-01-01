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

	@JsonProperty(value = "port", required = true)
	private String port;

	/*
	If we combined these, null would not be the same as undefined. In our config files, we want null to have the same meaning as
	undefined. So we use separate variables to allow this
	 */
	@JacksonInject(DEFAULT_SERIAL_CONFIG_KEY)
	private SerialConfig defaultSerialConfig;

	@JsonProperty("serial_config") // optional JSON override to provide custom configuration
	private SerialConfig serialConfig;

	@Override
	public IOBundle createIOBundle() throws Exception {
		SerialConfig serialConfig = this.serialConfig;
		if(serialConfig == null){
			serialConfig = defaultSerialConfig;
			if(serialConfig == null){
				throw new NullPointerException("serialConfig is null! This is likely because the injection didn't work properly.");
			}
		}
		String port = this.port;
		if(port == null){
			throw new NullPointerException("null is not a valid value for the port!");
		}
		return JSerialIOBundle.createPort(port, serialConfig);
	}
}
