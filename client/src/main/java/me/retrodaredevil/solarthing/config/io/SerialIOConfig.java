package me.retrodaredevil.solarthing.config.io;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fazecast.jSerialComm.SerialPort;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.JSerialIOBundle;
import me.retrodaredevil.io.serial.PureJavaCommIOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

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
	@JsonDeserialize(as = SerialConfigBuilderJackson.class)
	private SerialConfig serialConfig;

	@JsonProperty("purejavacomm")
	private boolean usePureJavaComm = false;

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
		if (usePureJavaComm) {
			return PureJavaCommIOBundle.create(port, serialConfig);
		}
		SerialPort serialPort = JSerialIOBundle.createSerialPortFromName(port);
		return new JSerialIOBundle(
				serialPort,
				serialConfig,
				new JSerialIOBundle.Config(50, 65536, 65536)
		);
	}
	@JsonExplicit
	static class SerialConfigBuilderJackson extends SerialConfigBuilder {

		public SerialConfigBuilderJackson() {
			super(-1);
		}

		@JsonProperty(value = "baud", required = true)
		@Override
		public SerialConfigBuilder setBaudRate(int baudRate) {
			return super.setBaudRate(baudRate);
		}

		@JsonProperty("data_bits")
		@Override
		public SerialConfigBuilder setDataBits(int dataBits) {
			return super.setDataBits(dataBits);
		}

		@JsonProperty("stop_bits")
		@Override
		public SerialConfigBuilder setStopBits(StopBits stopBits) {
			return super.setStopBits(stopBits);
		}

		@JsonProperty("parity")
		public void setParity(String parity){
			if(parity == null) return;

			setParity(Parity.EVEN);
			switch(parity){
				case "none":
					setParity(Parity.NONE);
					break;
				case "odd":
					setParity(Parity.ODD);
					break;
				case "mark":
					setParity(Parity.MARK);
				case "space":
					setParity(Parity.SPACE);
					break;
				default:
					throw new IllegalArgumentException("Unsupported parity=" + parity);
			}
		}

		@JsonProperty("rts")
		@Override
		public SerialConfigBuilder setRTS(boolean b) {
			return super.setRTS(b);
		}

		@JsonProperty("dtr")
		@Override
		public SerialConfigBuilder setDTR(boolean b) {
			return super.setDTR(b);
		}
	}
}
