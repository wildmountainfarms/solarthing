package me.retrodaredevil.solarthing.influxdb.infuxdb2;

import com.influxdb.client.write.Point;
import me.retrodaredevil.solarthing.influxdb.PointUtil;
import me.retrodaredevil.solarthing.packets.Packet;

public class ConstantMeasurementPacketPoint2Creator implements PacketPoint2Creator {
	private final String measurement;

	public ConstantMeasurementPacketPoint2Creator(String measurement) {
		this.measurement = measurement;
	}


	@Override
	public Point createBuilder(Packet packet) {
		Point r = Point.measurement(measurement);
		r.addTags(PointUtil.getTags(packet));
		return r;
	}
}
