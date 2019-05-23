package me.retrodaredevil.iot.outhouse;

public class IntegerWeatherPacket implements WeatherPacket{
	
	private final int temperatureCelsius;
	private final int humidityPercent;
	
	public IntegerWeatherPacket(int temperatureCelsius, int humidityPercent) {
		this.temperatureCelsius = temperatureCelsius;
		this.humidityPercent = humidityPercent;
	}
	
	@Override
	public Number getTemperatureCelsius() {
		return temperatureCelsius;
	}
	
	@Override
	public double getHumidity() {
		return temperatureCelsius / 100.0;
	}
	
	@Override
	public Number getHumidityPercent() {
		return humidityPercent;
	}
	
	@Override
	public OuthousePacketType getPacketType() {
		return OuthousePacketType.WEATHER;
	}
}
