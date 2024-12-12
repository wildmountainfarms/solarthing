package me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragmentMatcher;
import me.retrodaredevil.solarthing.packets.identification.IdentifierRepFragment;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.common.PVCurrentAndVoltage;

import static java.util.Objects.requireNonNull;


@JsonTypeName("packet")
public class PacketVoltageProvider implements VoltageProvider {
	private final IdentifierFragmentMatcher voltageIdentifierFragmentMatcher;
	private final VoltagePacketType voltagePacketType;

	@JsonCreator
	public PacketVoltageProvider(
			@JsonProperty(value = "identifier", required = true) @JsonDeserialize(as = IdentifierRepFragment.class) IdentifierFragmentMatcher voltageIdentifierFragmentMatcher,
			@JsonProperty("from") VoltagePacketType voltagePacketType) {
		this.voltageIdentifierFragmentMatcher = requireNonNull(voltageIdentifierFragmentMatcher);
		this.voltagePacketType = voltagePacketType == null ? VoltagePacketType.PV : voltagePacketType;
	}

	@Override
	public @Nullable Result getResult(FragmentedPacketGroup fragmentedPacketGroup) {
		for (Packet packet : fragmentedPacketGroup.getPackets()) {
			if (!(packet instanceof Identifiable)) {
				continue;
			}
			int fragmentId = fragmentedPacketGroup.getFragmentId(packet);
			IdentifierFragment identifierFragment = IdentifierFragment.create(fragmentId, ((Identifiable) packet).getIdentifier());
			if (!voltageIdentifierFragmentMatcher.matches(identifierFragment)) {
				continue;
			}
			long dateMillis = requireNonNull(fragmentedPacketGroup.getDateMillis(packet), "Implementation of FragmentedPacketGroup did not provide individual dateMillis! type: " + fragmentedPacketGroup.getClass().getName());
			if (voltagePacketType == VoltagePacketType.PV) {
				if (packet instanceof PVCurrentAndVoltage) {
					PVCurrentAndVoltage pvCurrentAndVoltage = (PVCurrentAndVoltage) packet;
					float voltage = pvCurrentAndVoltage.getPVVoltage().floatValue();
					return new Result(voltage, identifierFragment, dateMillis, false);
				}
			} else if (voltagePacketType == VoltagePacketType.BATTERY) {
				if (packet instanceof BatteryVoltage) {
					BatteryVoltage batteryVoltagePacket = (BatteryVoltage) packet;
					float batteryVoltage = batteryVoltagePacket.getBatteryVoltage();
					return new Result(batteryVoltage, identifierFragment, dateMillis, false);
				}
			} else throw new AssertionError();
		}

		return null;
	}
	public enum VoltagePacketType {
		PV,
		BATTERY
	}

}
