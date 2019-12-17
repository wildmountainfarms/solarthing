package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.creation.PacketCreationException;
import me.retrodaredevil.solarthing.packets.creation.TextPacketCreator;
import me.retrodaredevil.solarthing.packets.handling.RawPacketReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

public class SolarReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolarReader.class);
	
	private final InputStream in;
	private final TextPacketCreator creator;
	private final RawPacketReceiver rawPacketReceiver;

	private final byte[] buffer = new byte[1024];

	/**
	 * @param in The InputStream to read directly from
	 * @param packetCreator The packet creator that creates packets from bytes
	 */
	public SolarReader(InputStream in, TextPacketCreator packetCreator, RawPacketReceiver rawPacketReceiver) {
		this.in = requireNonNull(in);
		this.creator = requireNonNull(packetCreator);
		this.rawPacketReceiver = requireNonNull(rawPacketReceiver);
	}

	private boolean readData() throws IOException {
		// As of 2019.10.13, we can now detect EOF
		// stackoverflow: https://stackoverflow.com/q/53291868/5434860
		// (maybe) thanks to this solution: https://stackoverflow.com/questions/804951/is-it-possible-to-read-from-a-inputstream-with-a-timeout
		// As of <some time later>: I actually haven't tested this, so I don't know if this fixed the problem. However, this only matters while testing.

		int available = in.available();
		if(available < 0) throw new AssertionError("available cannot be less than 0! available: " + available);
		final int len;
		if(available == 0){ // if nothing is available, we want to try to see if we've reached the end of the file
			boolean isEOF = in.read(buffer, 0, 0) == -1;
			if(isEOF) {
				throw new EOFException();
			}
			rawPacketReceiver.updateNoNewData();
			return false;
		} else {
			len = in.read(buffer); // read as much as possible
			if(len == -1) throw new AssertionError("Because we call in.available(), len should never be -1. Did we change the code?");
		}
		String s = new String(buffer, 0, len);
		final Collection<? extends Packet> newPackets;
		try {
			newPackets = creator.add(s.toCharArray());
		} catch (PacketCreationException e) {
			e.printStackTrace();
			String debugString = s.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
			LOGGER.warn("Got a garbled packet! got: '" + debugString + "'");
			rawPacketReceiver.updateGarbledData();
			return true;
		}

		rawPacketReceiver.update(newPackets);
		return true;
	}

	/**
	 * Should be called continuously
	 */
	public void update() throws IOException {
		readData();
	}
}
