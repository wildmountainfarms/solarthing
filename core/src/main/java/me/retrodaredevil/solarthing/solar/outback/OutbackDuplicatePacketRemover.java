package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class OutbackDuplicatePacketRemover implements PacketListReceiver {
	public static final OutbackDuplicatePacketRemover INSTANCE = new OutbackDuplicatePacketRemover();
	private static final Logger LOGGER = LoggerFactory.getLogger(OutbackDuplicatePacketRemover.class);

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		OutbackStatusPacket last = null;
		List<Integer> split = new ArrayList<>();
		split.add(0);
		for (int i = 0; i < packets.size(); i++) {
			Packet packet = packets.get(i);
			if (packet instanceof OutbackStatusPacket) {
				OutbackStatusPacket outbackStatusPacket = (OutbackStatusPacket) packet;
				if(last != null && outbackStatusPacket.getAddress() <= last.getAddress()){
					split.add(i);
				}
				last = outbackStatusPacket;
			}
		}
		split.add(packets.size());
		if(split.size() > 2){
			Integer start = null;
			int end = 0;
			Integer lastIndex = null;
			for (int index : split) {
				if(lastIndex != null){
					int size = index - lastIndex;
					if(start == null || size >= end - start){
						end = index;
						start = lastIndex;
					}
				}
				lastIndex = index;
			}
			requireNonNull(start);
			LOGGER.debug("There were duplicate packets! packets: {}. We are shortening it to indexes: start=" + start + " end=" + end, packets);
			int i = 0;
			for(Iterator<Packet> it = packets.listIterator(); it.hasNext(); i++){
				it.next();
				if(i < start || i >= end){
					it.remove();
				}
			}
		}
	}
}
