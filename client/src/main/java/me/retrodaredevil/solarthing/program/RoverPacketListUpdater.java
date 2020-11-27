package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.io.modbus.ModbusRuntimeException;
import me.retrodaredevil.io.modbus.ModbusTimeoutException;
import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.misc.error.ImmutableExceptionErrorPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPackets;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RoverPacketListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverPacketListUpdater.class);
	private static final String MODBUS_RUNTIME_EXCEPTION_CATCH_LOCATION_IDENTIFIER = "rover.read.modbus";
	private static final String MODBUS_RUNTIME_INSTANCE_IDENTIFIER = "instance.1";

	private final RoverReadTable read;
	private final RoverWriteTable write;
	private final Runnable reloadCache;

	private final boolean isSendErrorPackets;

	private boolean hasBeenSuccessful = false;

	public RoverPacketListUpdater(RoverReadTable read, RoverWriteTable write, Runnable reloadCache, boolean isSendErrorPackets) {
		this.read = read;
		this.write = write;
		this.reloadCache = reloadCache;
		this.isSendErrorPackets = isSendErrorPackets;
	}

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		final long startTime = System.currentTimeMillis();
		final RoverStatusPacket packet;
		try {
			reloadCache.run();
			packet = RoverStatusPackets.createFromReadTable(read);
		} catch(ModbusRuntimeException e){
			LOGGER.error("Modbus exception", e);

			if (isSendErrorPackets) {
				LOGGER.debug("Sending error packets");
				packets.add(new ImmutableExceptionErrorPacket(
						e.getClass().getName(),
						e.getMessage(),
						MODBUS_RUNTIME_EXCEPTION_CATCH_LOCATION_IDENTIFIER,
						MODBUS_RUNTIME_INSTANCE_IDENTIFIER
				));
			}
			if (e instanceof ModbusTimeoutException) {
				// These messages will hopefully help people with problems fix it faster.
				if (hasBeenSuccessful) {
					LOGGER.info("\n\nHey! We noticed you got a ModbusTimeoutException after getting this to work.\n" +
							"This is likely a fluke and hopefully this message isn't printed a bunch of times. If it is, you may want to check your cable.\n");
				} else {
					LOGGER.info("\n\nHey! We noticed you got a ModbusTimeoutException.\n" +
							"This is likely a problem with your cable. SolarThing is communicating fine with your RS232 adapter, but it cannot reach the Rover.\n" +
							"Make sure you the cable you have has the correct pinout, and feel free to open an issue at https://github.com/wildmountainfarms/solarthing/issues if you need help.\n");
				}
			}
			return;
		}
		hasBeenSuccessful = true;
		LOGGER.debug("Debugging special power control values: (Will debug all packets later)\n" +
				packet.getSpecialPowerControlE021().getFormattedInfo().replaceAll("\n", "\n\t") + "\n" +
				packet.getSpecialPowerControlE02D().getFormattedInfo().replaceAll("\n", "\n\t")
		);
		packets.add(packet);
		final long readDuration = System.currentTimeMillis() - startTime;
		LOGGER.debug("took " + readDuration + "ms to read from Rover");
	}
}
