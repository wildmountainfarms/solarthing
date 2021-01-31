package me.retrodaredevil.solarthing.influxdb.infuxdb2;

import com.influxdb.client.write.Point;
import me.retrodaredevil.solarthing.influxdb.PointUtil;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;

public enum DocumentedMeasurementPacketPoint2Creator implements PacketPoint2Creator {
	INSTANCE;
	@Override
	public Point createBuilder(Packet packet) {
		if(packet instanceof DocumentedPacket){
			DocumentedPacket documentedPacket = (DocumentedPacket) packet;
			DocumentedPacketType type = documentedPacket.getPacketType();
			return apply(Point.measurement(type.toString()), packet);
		}
		return apply(Point.measurement(packet.getClass().getSimpleName()), packet);
	}
	private static Point apply(Point point, Packet packet) {
		point.addTags(PointUtil.getTags(packet));
		return point;
	}
}
