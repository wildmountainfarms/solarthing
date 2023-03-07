package me.retrodaredevil.solarthing.program.pvoutput.provider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.misc.source.W1Source;
import me.retrodaredevil.solarthing.misc.weather.TemperaturePacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragmentMatcher;
import me.retrodaredevil.solarthing.solar.common.BatteryTemperature;
import me.retrodaredevil.solarthing.solar.common.ControllerTemperature;

import static java.util.Objects.requireNonNull;

/**
 * Identifies a {@link TemperaturePacket} from a given {@link IdentifierFragmentMatcher}.
 * <p>
 * Most of the time the source of the temperature packet is
 */
public class TemperaturePacketTemperatureCelsiusProvider implements TemperatureCelsiusProvider {

	private final IdentifierFragmentMatcher temperatureIdentifierFragmentMatcher;
	private final TemperaturePacketType temperaturePacketType;

	@JsonCreator
	public TemperaturePacketTemperatureCelsiusProvider(
			@JsonProperty(value = "identifier", required = true) IdentifierFragmentMatcher temperatureIdentifierFragmentMatcher,
			@JsonProperty("from") TemperaturePacketType temperaturePacketType) {
		this.temperatureIdentifierFragmentMatcher = requireNonNull(temperatureIdentifierFragmentMatcher);
		this.temperaturePacketType = temperaturePacketType == null ? TemperaturePacketType.PACKET : temperaturePacketType;
	}

	@Override
	public @Nullable Result getResult(FragmentedPacketGroup fragmentedPacketGroup) {

		for (Packet packet : fragmentedPacketGroup.getPackets()) {
			if (!(packet instanceof Identifiable)) {
				continue;
			}
			int fragmentId = fragmentedPacketGroup.getFragmentId(packet);
			IdentifierFragment identifierFragment = IdentifierFragment.create(fragmentId, ((Identifiable) packet).getIdentifier());
			if (!temperatureIdentifierFragmentMatcher.matches(identifierFragment)) {
				continue;
			}
			long dateMillis = requireNonNull(fragmentedPacketGroup.getDateMillis(packet), "Implementation of FragmentedPacketGroup did not provide individual dateMillis! type: " + fragmentedPacketGroup.getClass().getName());
			if (temperaturePacketType == TemperaturePacketType.PACKET) {
				if (packet instanceof TemperaturePacket) {
					TemperaturePacket temperaturePacket = (TemperaturePacket) packet;
					boolean isW1Sensor = temperaturePacket.getDeviceSource() instanceof W1Source; // we assume W1 sensors sometimes give bad data

					float temperatureCelsius = temperaturePacket.getTemperatureCelsius();
					return new Result(temperatureCelsius, identifierFragment, dateMillis, isW1Sensor);
				}
			} else if (temperaturePacketType == TemperaturePacketType.CONTROLLER) {
				if (packet instanceof ControllerTemperature) {
					ControllerTemperature controllerTemperature = (ControllerTemperature) packet;
					return new Result(controllerTemperature.getControllerTemperatureCelsius().floatValue(), identifierFragment, dateMillis, false);
				}
			} else if (temperaturePacketType == TemperaturePacketType.BATTERY) {
				if (packet instanceof BatteryTemperature) {
					BatteryTemperature controllerTemperature = (BatteryTemperature) packet;
					return new Result(controllerTemperature.getBatteryTemperatureCelsius().floatValue(), identifierFragment, dateMillis, false);
				}
			}
		}
		return null;
	}
	public enum TemperaturePacketType {
		/** Refers to {@link TemperaturePacket#getTemperatureCelsius()}*/
		PACKET,
		/** Refers to {@link ControllerTemperature#getControllerTemperatureCelsius()}*/
		CONTROLLER,
		/** Refers to {@link BatteryTemperature#getBatteryTemperatureCelsius()}*/
		BATTERY
	}
}
