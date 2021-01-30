package me.retrodaredevil.solarthing.influxdb.influxdb1;

import me.retrodaredevil.solarthing.influxdb.PointUtil;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import org.influxdb.dto.Point;

import java.util.Map;

public class ConstantMeasurementPacketPointCreator implements PacketPointCreator {
	private final String measurement;

	public ConstantMeasurementPacketPointCreator(String measurement) {
		this.measurement = measurement;
	}

	@Override
	public Point.Builder createBuilder(Packet packet) {
		Point.Builder r = Point.measurement(measurement);
		for (Map.Entry<String, String> entry : PointUtil.getTags(packet).entrySet()) {
			r.tag(entry.getKey(), entry.getValue());
		}
		return r;
	}
}
