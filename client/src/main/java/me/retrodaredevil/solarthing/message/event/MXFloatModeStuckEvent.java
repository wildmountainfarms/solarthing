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

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * This event is useful for MX charge controllers with older firmware where they will be "stuck" in float mode once
 * they have entered float (usually after an EQ). This can alert you to manually get the MX out of float mode.
 * <p>
 * Update 2022.01.31 - Really all this alert indicates is that you have an MX with old firmware that is still in EQ mode.
 * I believe that some older MXs need to be manually turned out of EQ.
 */
@JsonTypeName("mxfloatstuck")
public class MXFloatModeStuckEvent implements MessageEvent {
	private static final Logger LOGGER = LoggerFactory.getLogger(MXFloatModeStuckEvent.class);

	private final Map<IdentifierFragment, Boolean> enabledMap = new HashMap<>();
	/** Map of {@link IdentifierFragment} to nano second values relative to a nanoTime() call */
	private final Map<IdentifierFragment, Long> lastNotifyMap = new HashMap<>();

	private final Duration timeout;

	@JsonCreator
	public MXFloatModeStuckEvent(
			@JsonProperty(value = "timeout", required = true) String timeoutDurationString
	) {
		this.timeout = Duration.parse(timeoutDurationString);
	}

	@Override
	public void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		for (Packet previousPacket : previous.getPackets()) {
			if (previousPacket instanceof MXStatusPacket previousMX) {
				KnownIdentifierFragment<OutbackIdentifier> identifierFragment = IdentifierFragment.create(previous.getFragmentId(previousPacket), previousMX.getIdentifier());
				MXStatusPacket currentMX = null;
				for (Packet currentPacket : current.getPackets()) {
					if (currentPacket instanceof Identifiable) {
						IdentifierFragment currentIdentifierFragment = IdentifierFragment.create(current.getFragmentId(currentPacket), ((Identifiable) currentPacket).getIdentifier());
						if (identifierFragment.equals(currentIdentifierFragment)) {
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
		Long lastNotifyNanos = lastNotifyMap.get(identifierFragment);
		long nowNanos = System.nanoTime(); // TODO use NanoTimeProvider
		if (lastNotifyNanos != null && nowNanos - lastNotifyNanos < timeout.toNanos()) {
			return; // timeout has not passed
		}
		LOGGER.debug("Float alert for " + identifierFragment);
		// alert
		sender.sendMessage("MX " + identifierFragment.getIdentifier().getAddress() + " is stuck in float mode. (Fragment " + identifierFragment.getFragmentId() + ") (Low priority)");
		enabledMap.put(identifierFragment, false); // don't notify until next day
		lastNotifyMap.put(identifierFragment, nowNanos);
	}
}
