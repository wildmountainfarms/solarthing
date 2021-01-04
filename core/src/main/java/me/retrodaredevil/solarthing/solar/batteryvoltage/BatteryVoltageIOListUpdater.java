package me.retrodaredevil.solarthing.solar.batteryvoltage;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BatteryVoltageIOListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(BatteryVoltageIOListUpdater.class);

	private final InputStream inputStream;
	private final int dataId;
	private final double multiplier;
	private final double invalidWhenBelow;
	private final double invalidWhenAbove;

	public BatteryVoltageIOListUpdater(InputStream inputStream, int dataId, double multiplier, double invalidWhenBelow, double invalidWhenAbove) {
		this.inputStream = inputStream;
		this.dataId = dataId;
		this.multiplier = multiplier;
		this.invalidWhenBelow = invalidWhenBelow;
		this.invalidWhenAbove = invalidWhenAbove;
	}

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		try {
			BatteryVoltageOnlyPacket packet = run();
			if (packet != null) {
				packets.add(packet);
			}
		} catch (IOException e) {
			LOGGER.error("Got error reading", e);
		}
	}

	private BatteryVoltageOnlyPacket run() throws IOException {
		StringBuilder resultBuilder = new StringBuilder();
		byte[] buffer = new byte[1024];
		while(inputStream.available() > 0) {
			int len = inputStream.read(buffer);
			resultBuilder.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
		}
		String result = resultBuilder.toString();
		String[] split = result.split("\n", -1);
		String lastSplit = split[split.length - 1];
		if (!lastSplit.isEmpty()) {
			LOGGER.debug("The last split was not a new line!");
			// we wont' do anything about this, it's fine. Because the voltage-sensor program spits out data fast enough, it should ignore the incomplete data next time
		}

		if (split.length >= 3) {
			String recentString = split[split.length - 2];
			String previousString = split[split.length - 3];

			Double recent = parseAndScale(recentString);
			Double previous = parseAndScale(previousString);
			if (recent == null || previous == null) {
				LOGGER.warn("Couldn't parse recent or previous! recentString=" + recentString + " previousString=" + previousString);
				return null;
			}
			if (Math.abs(recent - previous) > .3) {
				LOGGER.warn("Difference is too big! recent=" + recent + " previous=" + previous);
				return null;
			}
			if (recent < invalidWhenBelow) {
				LOGGER.warn("Battery Voltage is too small! " + recent);
				return null;
			}
			if (recent > invalidWhenAbove) {
				LOGGER.warn("Battery Voltage is too big! " + recent);
				return null;
			}
			LOGGER.debug("Most recent battery voltage: " + recent);
			return new ImmutableBatteryVoltageOnlyPacket(recent.floatValue(), dataId);
		}
		return null;
	}
	private Double parseAndScale(String string) {
		final double raw;
		try {
			raw = Double.parseDouble(string);
		} catch (NumberFormatException ex) {
			return null;
		}
		return raw * multiplier;
	}
}
