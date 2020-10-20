package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.packets.identification.KnownIdentifierFragment;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.mx.ChargerMode;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This event is useful for MX charge controllers with older firmware where they will be "stuck" in float mode once
 * they have entered float (usually after an EQ). This can alert you to manually get the MX out of float mode.
 */
@JsonTypeName("mxfloatstuck")
public class MXFloatModeStuckEvent implements MessageEvent {
	private static final Logger LOGGER = LoggerFactory.getLogger(MXFloatModeStuckEvent.class);

	private final Map<IdentifierFragment, Boolean> enabledMap = new HashMap<>();
	private final Map<IdentifierFragment, Long> lastNotifyMap = new HashMap<>();

	private final long timeoutMillis;

	@JsonCreator
	public MXFloatModeStuckEvent(@JsonProperty("timeout_hours") int timeoutHours) {
		this.timeoutMillis = timeoutHours * 60 * 60 * 1000;
	}

	@Override
	public void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		for (Packet packet : previous.getPackets()) {
			if (packet instanceof MXStatusPacket) {
				MXStatusPacket previousMX = (MXStatusPacket) packet;
				KnownIdentifierFragment<OutbackIdentifier> identifierFragment = IdentifierFragment.create(previous.getFragmentId(packet), previousMX.getIdentifier());
				MXStatusPacket currentMX = null;
				for (Packet currentPacket : current.getPackets()) {
					if (currentPacket instanceof Identifiable) {
						if (((Identifiable) currentPacket).getIdentifier().equals(identifierFragment.getIdentifier())) {
							currentMX = (MXStatusPacket) currentPacket;
						}
					}
				}
				if (currentMX == null) {
					continue;
				}
				if (currentMX.isNewDay(previousMX)) {
					enabledMap.put(identifierFragment, true);
				}
				if (enabledMap.getOrDefault(identifierFragment, false)) {
					ChargerMode mode = currentMX.getChargingMode();
					if (mode == ChargerMode.FLOAT) {
						doAlert(sender, identifierFragment);
					} else if (mode != ChargerMode.SILENT) {
						enabledMap.put(identifierFragment, false);
						LOGGER.debug("Disabling " + identifierFragment + " because mode is " + mode);
					}
				}
			}
		}
	}
	private void doAlert(MessageSender sender, KnownIdentifierFragment<OutbackIdentifier> identifierFragment) {
		Long lastNotify = lastNotifyMap.get(identifierFragment);
		long now = System.currentTimeMillis();
		if (lastNotify != null && lastNotify + timeoutMillis > now) {
			return; // timeout has not passed
		}
		LOGGER.debug("Float alert for " + identifierFragment);
		// alert
		sender.sendMessage("MX " + identifierFragment.getIdentifier().getAddress() + " is stuck in float mode. (Fragment " + identifierFragment.getFragmentId() + ") (Low priority)");
		enabledMap.put(identifierFragment, false); // don't notify until next day
		lastNotifyMap.put(identifierFragment, now);
	}
}
