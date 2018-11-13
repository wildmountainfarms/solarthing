package me.retrodaredevil.solarthing;

import org.lightcouch.CouchDbException;
import org.lightcouch.DocumentConflictException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.retrodaredevil.solarthing.packet.SolarPacket;

public class SolarReader {
	private static final long SAME_PACKET_COLLECTION_TIME = 250;

	private final int throttleFactor;
	private final InputStream in;
	//private ProgramArgs args;

	private final PacketCreator creator;
	private final PacketSaver packetSaver;


	public SolarReader(InputStream in, int throttleFactor, PacketCreator packetCreator, PacketSaver packetSaver) throws IOException {
		this.in = in;
		this.throttleFactor = throttleFactor;
		this.creator = packetCreator;
		this.packetSaver = packetSaver;
	}

	/**
	 * Takes over the current thread and runs forever
	 */
	public void start() {
		final List<SolarPacket> packetList = new ArrayList<>(); // a list that piles up SolarPackets and saves when needed // may be cleared
		long lastFirstReceivedData = Long.MIN_VALUE; // the last time a packet was added to packetList

		int packetCollectionCounter = -1;

		final byte[] buffer = new byte[1024];

		while(true) {
			try {
				// ======= read bytes, append to packetList =======
				int len;
				while (this.in.available() > 0 && (len = this.in.read(buffer)) > -1) {
					String s = new String(buffer, 0, len);
//					System.out.println("got characters: '" + s +"'");
					Collection<SolarPacket> newPackets = creator.add(s.toCharArray()); // usually null or size of 1 possibly size > 1

					long now = System.currentTimeMillis();
					if(lastFirstReceivedData + SAME_PACKET_COLLECTION_TIME < now) {
						lastFirstReceivedData = now; // set this to the first time we get bytes
					}

					if (newPackets != null) { // packets.length should never be 0 if it's not null
						packetList.addAll(newPackets);
					}
				}

				// ======= Save data if needed =======
				long now = System.currentTimeMillis();
				if (lastFirstReceivedData + SAME_PACKET_COLLECTION_TIME < now) { // if there's no packets coming any time soon
					if(!packetList.isEmpty()) {
						packetCollectionCounter++;
						// because packetCollectionCounter starts at -1, after above if statement, it will be >= 0
						if(packetCollectionCounter % throttleFactor == 0) {
							System.out.println("saving above packet(s). packetList.size(): " + packetList.size());
							packetSaver.savePackets(packetList);
							packetList.clear();
						} else {
							System.out.println("Not saving above packet(s) because " +
									"throttleFactor: " + throttleFactor +
									" packetCollectionCounter: " + packetCollectionCounter);
							packetList.clear(); // don't save these packets - ignore them
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("We got an IOException which doesn't happen often. We are going to try again so hopefully this works.");
			} catch(DocumentConflictException ex){
				ex.printStackTrace();
				System.err.println("Error while saving something to couchdb. Continuing like nothing happened now. " +
						"Your throttle factor (--tf) may be too low.");
			} catch(CouchDbException ex){
				ex.printStackTrace();
				System.err.println("We got a CouchDbException probably meaning we couldn't reach the database.");
			}
		}
	}
}
