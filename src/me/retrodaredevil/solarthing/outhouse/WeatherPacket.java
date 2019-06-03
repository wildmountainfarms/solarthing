package me.retrodaredevil.solarthing.outhouse;

public interface WeatherPacket extends OuthousePacket {
	/**
	 * Should be serialized as "temperatureCelsius"
	 * @return A non-null number representing the temperature in celsius
	 */
	Number getTemperatureCelsius();
	
	/**
	 * If serialized, should be serialized as "humidity"
	 * @return A number in range [0..1] representing the humidity
	 */
	double getHumidity();
	
	/**
	 * If serialized, should be serialized as "humidityPercent"
	 * @return A number in range [0..100] representing the humidity
	 */
	Number getHumidityPercent();
}
