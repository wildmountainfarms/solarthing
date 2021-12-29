package me.retrodaredevil.solarthing.program.receiver;

import me.retrodaredevil.io.modbus.ModbusMessage;
import me.retrodaredevil.io.modbus.ModbusRuntimeException;
import me.retrodaredevil.io.modbus.ModbusTimeoutException;
import me.retrodaredevil.io.modbus.handling.ParsedResponseException;
import me.retrodaredevil.io.modbus.handling.RawResponseException;
import me.retrodaredevil.solarthing.config.request.modbus.SuccessReporter;
import me.retrodaredevil.solarthing.io.NotInitializedIOException;
import me.retrodaredevil.solarthing.misc.error.ImmutableExceptionErrorPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ModbusListUpdaterWrapper implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModbusListUpdaterWrapper.class);
	private static final String MODBUS_RUNTIME_EXCEPTION_CATCH_LOCATION_IDENTIFIER = "read.modbus";

	private final PacketListReceiver packetListReceiver;
	private final Runnable reloadCache;
	private final SuccessReporter successReporter;

	private final boolean isSendErrorPackets;
	private final String errorIdentifierString;

	private boolean hasBeenSuccessful = false;

	public ModbusListUpdaterWrapper(PacketListReceiver packetListReceiver, Runnable reloadCache, SuccessReporter successReporter, boolean isSendErrorPackets, String errorIdentifierString) {
		this.packetListReceiver = packetListReceiver;
		this.reloadCache = reloadCache;
		this.successReporter = successReporter;
		this.isSendErrorPackets = isSendErrorPackets;
		this.errorIdentifierString = errorIdentifierString;
	}
	private static String dataToSplitHex(byte[] data) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (byte element : data) {
			if (first) {
				first = false;
			} else {
				builder.append(' '); // separate each byte with space
			}
			builder.append(String.format("%02X", element & 0xFF));
		}
		return builder.toString();
	}

	@Override
	public void receive(List<Packet> packets) {
		final long startTimeNanos = System.nanoTime();
		try {
			reloadCache.run();
			packetListReceiver.receive(packets);
		} catch(ModbusRuntimeException e){
			LOGGER.error("Modbus exception", e);

			if (isSendErrorPackets) {
				LOGGER.debug("Sending error packets");
				packets.add(new ImmutableExceptionErrorPacket(
						e.getClass().getName(),
						e.getMessage(),
						MODBUS_RUNTIME_EXCEPTION_CATCH_LOCATION_IDENTIFIER,
						errorIdentifierString
				));
			}
			boolean isTimeout = false;
			if (e.getCause() instanceof NotInitializedIOException) {
				isTimeout = true;
			}
			if (e instanceof ModbusTimeoutException) {
				isTimeout = true;
				// These messages will hopefully help people with problems fix it faster.
				if (hasBeenSuccessful) {
					LOGGER.info("\n\nHey! We noticed you got a ModbusTimeoutException after getting this to work.\n" +
							"This is likely a fluke and hopefully this message isn't printed a bunch of times. If it is not a fluke, you may want to check your cable.\n");
				} else {
					LOGGER.info("\n\nHey! We noticed you got a ModbusTimeoutException.\n" +
							"This is likely a problem with your cable. SolarThing is communicating fine with your serial adapter, but it cannot reach the device.\n" +
							"Make sure the cable you have has the correct pinout, and feel free to open an issue at https://github.com/wildmountainfarms/solarthing/issues if you need help.\n");
				}
			} else if (e instanceof ParsedResponseException) {
				ParsedResponseException parsedResponseException = (ParsedResponseException) e;
				ModbusMessage message = parsedResponseException.getResponse();
				String hexFunctionCode = String.format("%02X", message.getFunctionCode());
				LOGGER.info("Communication with rover working well. Got this response back: function code=0x" + hexFunctionCode + " data='" + dataToSplitHex(message.getByteData()) + "' feel free to open issue at https://github.com/wildmountainfarms/solarthing/issues/");
			} else if (e instanceof RawResponseException) {
				byte[] data = ((RawResponseException) e).getRawData();
				LOGGER.info("Got part of a response back. (Maybe timed out halfway through?) data='" + dataToSplitHex(data) + "' Feel free to open an issue at https://github.com/wildmountainfarms/solarthing/issues/");
			}
			if (isTimeout) {
				successReporter.reportTimeout();
			} else {
				successReporter.reportSuccessWithError();
			}
			return;
		}
		hasBeenSuccessful = true;
		successReporter.reportSuccess();
		final long readDurationNanos = System.nanoTime() - startTimeNanos;
		LOGGER.debug("took " + TimeUtil.nanosToSecondsString(readDurationNanos) + " seconds to read from device");
	}
}
