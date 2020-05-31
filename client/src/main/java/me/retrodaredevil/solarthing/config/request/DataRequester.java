package me.retrodaredevil.solarthing.config.request;

import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

public interface DataRequester {

	PacketListReceiver getPacketListReceiver();
}
