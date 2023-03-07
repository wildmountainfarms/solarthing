package me.retrodaredevil.solarthing.program.pvoutput.provider;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragmentMatcher;
import me.retrodaredevil.solarthing.solar.common.PVCurrentAndVoltage;

import static java.util.Objects.requireNonNull;


public class PVVoltageProvider implements VoltageProvider {
	private final IdentifierFragmentMatcher voltageIdentifierFragmentMatcher;

	public PVVoltageProvider(IdentifierFragmentMatcher voltageIdentifierFragmentMatcher) {
		this.voltageIdentifierFragmentMatcher = voltageIdentifierFragmentMatcher;
	}

	@Override
	public @Nullable Result getResult(FragmentedPacketGroup fragmentedPacketGroup) {
		for (Packet packet : fragmentedPacketGroup.getPackets()) {
			long dateMillis = requireNonNull(fragmentedPacketGroup.getDateMillis(packet), "Implementation of FragmentedPacketGroup did not provide individual dateMillis! type: " + fragmentedPacketGroup.getClass().getName());
			if (packet instanceof PVCurrentAndVoltage) {
				int fragmentId = fragmentedPacketGroup.getFragmentId(packet);
				PVCurrentAndVoltage pvCurrentAndVoltage = (PVCurrentAndVoltage) packet;
				IdentifierFragment identifierFragment = IdentifierFragment.create(fragmentId, pvCurrentAndVoltage.getIdentifier());
				if (voltageIdentifierFragmentMatcher.matches(identifierFragment)) {
					float voltage = pvCurrentAndVoltage.getPVVoltage().floatValue();
					return new Result(voltage, identifierFragment, dateMillis, false);
				}
			}
		}
		return null;
	}
}
