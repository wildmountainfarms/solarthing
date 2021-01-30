package me.retrodaredevil.solarthing.influxdb.influxdb1;

import me.retrodaredevil.solarthing.packets.Packet;
import org.influxdb.dto.Point;

public interface PacketPointCreator {
	/**
	 * @param packet The packet
	 * @return A point builder that should have a defined measurement and tags. You should not add fields to this.
	 */
	Point.Builder createBuilder(Packet packet);
}
