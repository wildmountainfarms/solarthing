package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;

import java.time.Duration;

@JsonTypeName("temperature")
public class TemperatureEvent implements MessageEvent {
	private final boolean low;
	private final boolean displayFahrenheit;
	private final float thresholdCelsius;
	private final long timeoutMillis;
	private final TemperatureType temperatureType;

	private Long lastSend = null;

	public TemperatureEvent(
			@JsonProperty("low") Boolean low,
			@JsonProperty("celsius") Float thresholdCelsius,
			@JsonProperty("fahrenheit") Float thresholdFahrenheit,
			@JsonProperty(value = "timeout", required = true) String timeoutDurationString,
			@JsonProperty("from") TemperatureType temperatureType) {
		this.low = low != null && low; // default to high temperature
		if (thresholdCelsius != null) {
			this.thresholdCelsius = thresholdCelsius;
			displayFahrenheit = false;
		} else if (thresholdFahrenheit != null) {
			this.thresholdCelsius = (thresholdFahrenheit - 32) / 1.8f;
			displayFahrenheit = true;
		} else {
			throw new IllegalArgumentException("Either celsius or fahrenheit must be defined!");
		}
		this.timeoutMillis = Duration.parse(timeoutDurationString).toMillis();
		if (temperatureType == null) {
			this.temperatureType = TemperatureType.BATTERY;
		} else {
			this.temperatureType = temperatureType;
		}
	}

	@Override
	public void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		Long lastSend = this.lastSend;
		if (lastSend != null) {
			if (lastSend + timeoutMillis > System.currentTimeMillis()) {
				return;
			}
		}
		for (Packet packet : current.getPackets()) {
			if (packet instanceof RoverStatusPacket) {
				RoverStatusPacket rover = (RoverStatusPacket) packet;
				if (temperatureType == TemperatureType.BATTERY) {
					if (check(sender, rover.getBatteryTemperatureCelsius())) {
						return;
					}
				} else if (temperatureType == TemperatureType.CONTROLLER) {
					if (check(sender, rover.getControllerTemperatureCelsius())) {
						return;
					}
				} else { // either
					if (check(sender, rover.getBatteryTemperatureCelsius()) || check(sender, rover.getControllerTemperatureCelsius())) {
						return;
					}
				}
			}
		}
	}
	private String temperatureToString(float temperatureCelsius) {
		if (displayFahrenheit) {
			return (temperatureCelsius * 1.8 + 32) + "\u2109";
		}
		return temperatureCelsius + "\u2103";
	}
	private boolean check(MessageSender sender, float temperatureCelsius) {
		if (low) {
			if (temperatureCelsius <= thresholdCelsius) {
				sender.sendMessage("Low temperature! " + temperatureToString(temperatureCelsius));
				lastSend = System.currentTimeMillis();
				return true;
			}
		} else {
			if (temperatureCelsius >= thresholdCelsius) {
				sender.sendMessage("High temperature! " + temperatureToString(temperatureCelsius));
				lastSend = System.currentTimeMillis();
				return true;
			}
		}
		return false;
	}

	public enum TemperatureType {
		BATTERY,
		CONTROLLER,
		EITHER
	}
}
