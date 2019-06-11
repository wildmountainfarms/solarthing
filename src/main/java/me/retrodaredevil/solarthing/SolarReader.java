package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.PacketCreator;
import me.retrodaredevil.solarthing.packets.PacketSaveException;
import me.retrodaredevil.solarthing.packets.PacketSaver;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class SolarReader implements Runnable{
	private static final long SAME_PACKET_COLLECTION_TIME = 250;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	private final int throttleFactor;
	private final InputStream in;
	private final PacketCreator creator;
	private final PacketSaver packetSaver;
	private final PacketCollectionIdGenerator idGenerator;
	
	
	private final List<Packet> packetList = new ArrayList<>(); // a list that piles up SolarPackets and saves when needed // may be cleared
	private long lastFirstReceivedData = Long.MIN_VALUE; // the last time a packet was added to packetList
	private int packetCollectionCounter = -1;
	private final byte[] buffer = new byte[1024];

	/**
	 *  @param in The InputStream to read directly from
	 * @param throttleFactor Will save every nth packet where n is this number
	 * @param packetCreator The packet creator that creates packets from bytes
	 * @param packetSaver The packet saver that saves a collection of packets at once
	 * @param idGenerator The {@link PacketCollectionIdGenerator} used to get the id to save packets with
	 */
	public SolarReader(InputStream in, int throttleFactor, PacketCreator packetCreator, PacketSaver packetSaver, PacketCollectionIdGenerator idGenerator) {
		this.in = in;
		this.throttleFactor = throttleFactor;
		this.creator = packetCreator;
		this.packetSaver = packetSaver;
		this.idGenerator = idGenerator;
	}
	
	/**
	 * Should be called continuously
	 */
	@Override
	public void run() {
		// This implementation isn't perfect - we cannot detect EOF
		// stackoverflow: https://stackoverflow.com/q/53291868/5434860
		int len = 0;
		try {
			
			// ======= read bytes, append to packetList =======
			while (in.available() > 0 && (len = in.read(buffer)) > -1) {
				String s = new String(buffer, 0, len);
//					System.out.println("got: '" + s.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r") + "'. len: " + len);
				Collection<? extends Packet> newPackets = creator.add(s.toCharArray());
				
				long now = System.currentTimeMillis();
				if(lastFirstReceivedData + SAME_PACKET_COLLECTION_TIME < now) {
					lastFirstReceivedData = now; // set this to the first time we get bytes
				}
				packetList.addAll(newPackets);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("We got an IOException which doesn't happen often. We are going to try again so hopefully this works.");
			return;
		}
		if(len == -1) throw new AssertionError("Because we call in.available(), len should never be -1. Did we change the code?");
		
		// ======= Save data if needed =======
		long now = System.currentTimeMillis();
		if (lastFirstReceivedData + SAME_PACKET_COLLECTION_TIME < now) { // if there's no new packets coming any time soon
			if(!packetList.isEmpty()) {
				packetCollectionCounter++;
				// because packetCollectionCounter starts at -1, after above if statement, it will be >= 0
				try {
					if (packetCollectionCounter % throttleFactor == 0) {
						System.out.println("saving above packet(s). packetList.size(): " + packetList.size());
						packetSaver.savePacketCollection(PacketCollections.createFromPackets(packetList, idGenerator));
					} else {
						System.out.println("Not saving above packet(s) because" +
							" throttleFactor: " + throttleFactor +
							" packetCollectionCounter: " + packetCollectionCounter);
					}
				} catch(PacketSaveException ex){
					System.err.println();
					System.err.println(DATE_FORMAT.format(Calendar.getInstance().getTime()));
					ex.printStackTrace();
					System.err.println("Was unable to save " + packetList.size() + " packets.");
					System.err.println();
				} finally {
					packetList.clear();
				}
			}
		}
	}
}
