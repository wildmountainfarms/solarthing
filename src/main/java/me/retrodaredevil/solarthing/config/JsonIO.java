package me.retrodaredevil.solarthing.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.JSerialIOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.io.serial.SerialPortException;

public class JsonIO {
	public static IOBundle getSerialIOBundleFromJson(JsonObject jsonObject, SerialConfig defaultSerialConfig) throws SerialPortException {
		String port = jsonObject.get("port").getAsString();
		JsonElement serialConfigElement = jsonObject.get("serial_config");
		final JsonObject serialConfigObject;
		if(serialConfigElement == null || serialConfigElement.isJsonNull()){
			serialConfigObject = null;
		} else {
			serialConfigObject = serialConfigElement.getAsJsonObject();
		}
		final SerialConfig serialConfig;
		if(serialConfigObject == null){
			serialConfig = defaultSerialConfig;
		} else {
			SerialConfigBuilder builder = new SerialConfigBuilder(serialConfigObject.get("baud").getAsInt());
			JsonElement stopBitsElement = serialConfigObject.get("stop_bits");
			if(stopBitsElement != null){
				double stopBitsValue = stopBitsElement.getAsDouble();
				final SerialConfig.StopBits stopBits;
				if(stopBitsValue == 1){
					stopBits = SerialConfig.StopBits.ONE;
				} else if(stopBitsValue == 1.5){
					stopBits = SerialConfig.StopBits.ONE_POINT_FIVE;
				} else if(stopBitsValue == 2){
					stopBits = SerialConfig.StopBits.TWO;
				} else {
					throw new IllegalArgumentException("Unknown stop bits value: " + stopBitsValue);
				}
				builder.setStopBits(stopBits);
			}
			serialConfig = builder.build();
		}
		return JSerialIOBundle.createPort(port, serialConfig);
	}
}
