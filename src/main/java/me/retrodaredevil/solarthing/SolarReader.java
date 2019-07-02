package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.PacketCreator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class SolarReader implements Runnable{
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	
	private final InputStream in;
	private final PacketCreator creator;
	private final PacketHandler packetHandler;
	private final PacketCollectionIdGenerator idGenerator;
	private final long samePacketTime;
	
	private final List<Packet> packetList = new ArrayList<>(); // a list that piles up SolarPackets and handles when needed // may be cleared
	private long lastFirstReceivedData = Long.MIN_VALUE; // the last time a packet was added to packetList
	private boolean instant = false;
	private final byte[] buffer = new byte[1024];

	/**
	 * @param in The InputStream to read directly from
	 * @param packetCreator The packet creator that creates packets from bytes
	 * @param packetHandler The packet handler that handles a {@link me.retrodaredevil.solarthing.packets.collection.PacketCollection} {@code samePacketTime} millis after the last packet has been created
	 * @param idGenerator The {@link PacketCollectionIdGenerator} used to get the id to save packets with
	 * @param samePacketTime The maximum amount of time allowed between packets that will be grouped together in a {@link me.retrodaredevil.solarthing.packets.collection.PacketCollection}
	 */
	public SolarReader(InputStream in, PacketCreator packetCreator, PacketHandler packetHandler, PacketCollectionIdGenerator idGenerator, long samePacketTime) {
		this.in = in;
		this.creator = packetCreator;
		this.packetHandler = packetHandler;
		this.idGenerator = idGenerator;
		this.samePacketTime = samePacketTime;
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
				if(lastFirstReceivedData + samePacketTime < now) {
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
		
		// ======= Handle data if needed =======
		long now = System.currentTimeMillis();
		if (lastFirstReceivedData + samePacketTime < now) { // if there's no new packets coming any time soon
			if (packetList.isEmpty()) {
				if(len == 0) {
					instant = true;
				}
			} else {
				final boolean wasInstant = instant;
				instant = false;
				try {
					System.out.println("handling above packet(s). packetList.size(): " + packetList.size() + " instant: " + wasInstant);
					packetHandler.handle(PacketCollections.createFromPackets(packetList, idGenerator), wasInstant);
				} catch(PacketHandleException ex){
					System.err.println();
					System.err.println(DATE_FORMAT.format(Calendar.getInstance().getTime()));
					ex.printStackTrace();
					System.err.println("Was unable to handle " + packetList.size() + " packets.");
					System.err.println();
				} finally {
					packetList.clear();
				}
			}
		}
	}
}
