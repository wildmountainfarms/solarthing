package me.retrodaredevil.solarthing.solar;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.common.DailyChargeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link PacketListReceiver} which will summary log when a charge controller has a new day. This is useful
 * to make sure that the daily kWh of a charge controller is logged somewhere permanent in case the connection to the databaes does
 * not work for a long period of time.
 */
public class DaySummaryLogListReceiver implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(DaySummaryLogListReceiver.class);
	private final Map<Identifier, DailyChargeController> controllerMap = new HashMap<>();

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		for (Packet packet : packets) {
			if (packet instanceof DailyChargeController) {
				DailyChargeController controller = (DailyChargeController) packet;
				DailyChargeController previous = controllerMap.get(controller.getIdentifier());
				controllerMap.put(controller.getIdentifier(), controller);
				if (previous != null && controller.isNewDay(previous)) {
					LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Controller: " + previous.getIdentifier().getRepresentation() + " day end! kWh: " + previous.getDailyKWH());
				}
			}
		}
	}
}
