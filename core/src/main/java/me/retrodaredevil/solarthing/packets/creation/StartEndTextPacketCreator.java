package me.retrodaredevil.solarthing.packets.creation;

import me.retrodaredevil.solarthing.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class StartEndTextPacketCreator implements TextPacketCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(StartEndTextPacketCreator.class);
	private final char start, end;
	private final int assertSizeAtLeast;
	private final char[] bytes;

	private int amount;
	/**
	 *
	 * @param start The char indicating a packet start
	 * @param end The char indicating a packet end
	 * @param maxPacketSize The maximum number of characters in a packet including start and end characters
	 * @param assertSizeAtLeast The minimum number of characters allowed in a packet including start and and characters.
	 *                          If this is not satisfied, a {@link PacketCreationException} will be thrown from {@link #add(char[])}
	 */
	public StartEndTextPacketCreator(char start, char end, int maxPacketSize, int assertSizeAtLeast) {
		this.start = start;
		this.end = end;
		this.assertSizeAtLeast = assertSizeAtLeast;
		bytes = new char[maxPacketSize];
	}

	@Override
	public Collection<Packet> add(char[] chars) throws PacketCreationException{
		if(chars.length == 0){
			return Collections.emptySet();
		}
		char first = chars[0];
		if(amount == 0 && first != start){
			return Collections.emptySet(); // gotta wait for the start char
		}
		List<Packet> r = null;
		for(char c : chars){
			if(amount >= bytes.length) {
				try {
					throw new PacketTooBigException("The packet will be longer than the maximum size!");
				} finally {
					reset();
				}
			}
			bytes[amount] = c;
			amount++;
			if(c == end){
				if(amount < assertSizeAtLeast){
					try {
						throw new PacketTooSmallException("amount is less than the minimum required size! amount: " + amount + " assertSizeAtLeast: " + assertSizeAtLeast);
					} finally {
						reset();
					}
				}
				try{
					final Collection<Packet> packetsToAdd = create(new String(bytes, 0, amount).toCharArray()); // resets bytes array and amount

					if(r == null){
						r = new ArrayList<>();
					}
					r.addAll(packetsToAdd);
				} catch(PacketCreationException ex){
					LOGGER.error("Got an exception that we were able to handle. Ignoring it...", ex);
				} finally {
					reset();
				}

			} else if(c == start){
				reset();
				bytes[0] = start;
				amount++;
			}
		}
		if(r == null){
			return Collections.emptySet();
		}
		return r;
	}

	/**
	 * @param bytes The bytes to create the packet(s) with
	 * @return A Collection usually with a size of 1 representing the packets to add.
	 * @throws PacketCreationException Should be thrown if something unexpected happens where the program should not crash
	 */
	protected abstract Collection<Packet> create(char[] bytes) throws PacketCreationException;

	private void reset(){
		amount = 0;
		// reset bytes array
		Arrays.fill(bytes, (char) 0);
	}
}
