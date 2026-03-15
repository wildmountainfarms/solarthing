package me.retrodaredevil.solarthing.influxdb.influxdb1;

import me.retrodaredevil.solarthing.influxdb.PointUtil;
import me.retrodaredevil.solarthing.packets.Packet;
import org.influxdb.dto.Point;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
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
