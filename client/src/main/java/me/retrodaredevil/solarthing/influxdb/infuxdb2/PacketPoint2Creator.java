package me.retrodaredevil.solarthing.influxdb.infuxdb2;

import com.influxdb.client.write.Point;
import me.retrodaredevil.solarthing.packets.Packet;

public interface PacketPoint2Creator {
	Point createBuilder(Packet packet);
}
