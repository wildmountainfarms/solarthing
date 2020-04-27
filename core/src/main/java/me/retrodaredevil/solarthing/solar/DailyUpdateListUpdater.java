package me.retrodaredevil.solarthing.solar;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.extra.ImmutableDailyUpdatePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class DailyUpdateListUpdater<T extends Identifiable> implements PacketListReceiver {

	@Deprecated
	public static final PacketListReceiver MX_DAILY_UPDATE_LIST_UPDATER = new DailyUpdateListUpdater<>(MXStatusPacket.class, mxStatusPacket -> {
		Support dailyAHSupport = mxStatusPacket.getDailyAHSupport();
		if (dailyAHSupport == Support.NOT_SUPPORTED) {
			return mxStatusPacket.getDailyKWH();
		}
		int ah = mxStatusPacket.getDailyAH();
		if (dailyAHSupport != Support.FULLY_SUPPORTED && ah == 9999) {
			return mxStatusPacket.getDailyKWH();
		}
		return mxStatusPacket.getDailyKWH() * 10000 + ah; // this is arbitrary
	});

	private final Class<T> clazz;
	private final TotalProvider<T> totalProvider;

	private final Map<Identifier, Node<T>> nodeMap = new HashMap<>();

	public DailyUpdateListUpdater(Class<T> clazz, TotalProvider<T> totalProvider) {
		this.clazz = clazz;
		this.totalProvider = totalProvider;
	}

	@Override
	public void receive(List<Packet> packets, boolean wasInstant) {
		long now = System.currentTimeMillis();
		for(Packet packet : packets){
			if(clazz.isInstance(packet)){
				T current = clazz.cast(packet);
				Node<T> node = nodeMap.get(current.getIdentifier());
				if (node == null) {
					node = new Node<>();
					nodeMap.put(current.getIdentifier(), node);
					node.monitorStartTimeMillis = now;
				}
				T previous = node.previous;
				node.previous = current;

				double total = totalProvider.getTotal(current);
				if (previous != null) {
					double previousTotal = totalProvider.getTotal(previous);
					if (previousTotal > total) {
						node.lastResetTimeMillis = now;
						node.lastAtZeroTimeMillis = now;
					} else if (total > previousTotal) {
						node.lastIncrementTimeMillis = now;
					}
				}
				if (total == 0) {
					node.lastAtZeroTimeMillis = now;
				}
				packets.add(new ImmutableDailyUpdatePacket(node.lastResetTimeMillis, node.lastAtZeroTimeMillis, node.lastIncrementTimeMillis, node.monitorStartTimeMillis, current.getIdentifier(), current.getIdentityInfo()));
			}
		}
	}
	@FunctionalInterface
	public interface TotalProvider<T extends Identifiable> {
		double getTotal(T t);
	}
	private static class Node<T> {
		T previous;
		@Nullable Long lastResetTimeMillis;
		@Nullable Long lastAtZeroTimeMillis;
		@Nullable Long lastIncrementTimeMillis;
		long monitorStartTimeMillis;
	}

}
