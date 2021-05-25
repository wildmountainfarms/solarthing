package me.retrodaredevil.solarthing.influxdb.influxdb1;

import me.retrodaredevil.solarthing.influxdb.PointUtil;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import org.influxdb.dto.Point;

import java.util.Map;

public enum DocumentedMeasurementPacketPointCreator implements PacketPointCreator {
	INSTANCE;
	@Override
	public Point.Builder createBuilder(Packet packet) {
		if(packet instanceof DocumentedPacket){
			DocumentedPacket documentedPacket = (DocumentedPacket) packet;
			DocumentedPacketType type = documentedPacket.getPacketType();
			return apply(Point.measurement(type.toString()), packet);
		}
		return apply(Point.measurement(packet.getClass().getSimpleName()), packet);
	}
	private static Point.Builder apply(Point.Builder point, Packet packet) {
		for (Map.Entry<String, String> entry : PointUtil.getTags(packet).entrySet()) {
			point.tag(entry.getKey(), entry.getValue());
		}
		return point;
	}
}
