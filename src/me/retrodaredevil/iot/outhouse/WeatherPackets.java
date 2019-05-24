package me.retrodaredevil.iot.outhouse;

import com.google.gson.JsonObject;

public final class WeatherPackets {
	private WeatherPackets(){ throw new UnsupportedOperationException(); }
	
	public static WeatherPacket createFromJson(JsonObject jsonObject){
		int temperatureCelsius = jsonObject.getAsJsonPrimitive("temperatureCelsius").getAsInt();
		int humidityPercent = jsonObject.getAsJsonPrimitive("humidityPercent").getAsInt();
		return new IntegerWeatherPacket(temperatureCelsius, humidityPercent);
	}
}
