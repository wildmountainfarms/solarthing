package me.retrodaredevil.solarthing.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.serial.JSerialIOBundle;
import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.io.serial.SerialPortException;

public final class JsonIO {
	private JsonIO(){ throw new UnsupportedOperationException(); }
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
			JsonElement dataBitsElement = serialConfigObject.get("data_bits");
			if(dataBitsElement != null){
				builder.setDataBits(dataBitsElement.getAsInt());
			} else builder.setDataBits(defaultSerialConfig.getDataBitsValue());
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
			} else builder.setStopBits(defaultSerialConfig.getStopBits());
			JsonElement parityElement = serialConfigObject.get("parity");
			if(parityElement != null){
				String parityName = parityElement.getAsString();
				final SerialConfig.Parity parity;
				switch(parityName){
					case "none": parity = SerialConfig.Parity.NONE; break;
					case "odd": parity = SerialConfig.Parity.ODD; break;
					case "even": parity = SerialConfig.Parity.EVEN; break;
					case "mark": parity = SerialConfig.Parity.MARK; break;
					case "space": parity = SerialConfig.Parity.SPACE; break;
					default: throw new UnsupportedOperationException("Unknown parity: " + parityName);
				}
				builder.setParity(parity);
			} else builder.setParity(defaultSerialConfig.getParity());
			JsonElement rtsElement = serialConfigObject.get("rts");
			if(rtsElement != null){
				builder.setRTS(rtsElement.getAsBoolean());
			} else {
				builder.setRTS(defaultSerialConfig.isRTS());
			}
			JsonElement dtrElement = serialConfigObject.get("dtr");
			if(dtrElement != null){
				builder.setDTR(dtrElement.getAsBoolean());
			} else {
				builder.setDTR(defaultSerialConfig.isDTR());
			}
			serialConfig = builder.build();
		}
		return JSerialIOBundle.createPort(port, serialConfig);
	}
}
