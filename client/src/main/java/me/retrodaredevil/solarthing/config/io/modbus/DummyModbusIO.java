package me.retrodaredevil.solarthing.config.io.modbus;

import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.modbus.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import static java.util.Objects.requireNonNull;

public class DummyModbusIO implements IOBundle {
	private static final Logger LOGGER = LoggerFactory.getLogger(DummyModbusIO.class);
	/*
	Just a note: In the past, there have been reports of bugs regarding the use of System.nanoTime() across threads.
	Those bugs should not be present, so using System.nanoTime() *should* be safe.
	 */

	private final Map<Integer, ModbusSlave> addressToSlaveMap;
	private final IODataEncoder ioDataEncoder;

	private final Queue<Byte> outputData = new ConcurrentLinkedDeque<>();
	private final Queue<Byte> inputData = new ConcurrentLinkedDeque<>();
	private transient boolean isClosed = false;
	private transient Long lastWriteNanos = null;

	public DummyModbusIO(Map<Integer, ModbusSlave> addressToSlaveMap, IODataEncoder ioDataEncoder) {
		this.addressToSlaveMap = addressToSlaveMap;
		this.ioDataEncoder = ioDataEncoder;

		Thread thread = new Thread(this::runThread);
		thread.setDaemon(true);
		thread.start();
	}

	private void runThread() {
		while (!Thread.currentThread().isInterrupted() && !isClosed) {
			long nowNanos = System.nanoTime();
			Long lastWriteNanos = this.lastWriteNanos;
			if (lastWriteNanos == null || nowNanos - lastWriteNanos > 6_000_000) { // 6ms have passed since the last write
				byte[] data = new byte[inputData.size()];
				// RtuDataEncoder requires at least 4 bytes to do something useful, so if we don't have that, just wait
				//   Also note that if data.length is 0, then the current implementation of RtuDataEncoder would hang because the call to available() would stay 0
				if (data.length >= 4) {
					for (int i = 0; i < data.length; i++) {
						int value = requireNonNull(inputData.poll(), "Should not have gotten null! i: " + i + " length: " + data.length);
						data[i] = (byte) value;
					}
					AddressedModbusMessage requestMessage = null;
					try {
						requestMessage = ioDataEncoder.parseMessage(data);
					} catch (RedundancyException ex) {
						LOGGER.debug("Got redundancy exception", ex);
					}
					ModbusMessage responseMessage = null;
					Integer address = null;
					if (requestMessage != null) {
						address = requestMessage.getAddress();
						ModbusSlave slave = addressToSlaveMap.get(address);
						if (slave == null) {
							LOGGER.warn("No corresponding slave for address: " + address);
						} else {
							responseMessage = slave.sendRequestMessage(requestMessage.getModbusMessage());
						}
					}
					if (responseMessage != null) {
						//noinspection ConstantConditions
						assert address != null;
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						ioDataEncoder.sendMessage(outputStream, address, responseMessage);
						byte[] responseBytes = outputStream.toByteArray();
						for (byte b : responseBytes) {
							outputData.add(b);
						}
					} else {
						LOGGER.debug("Didn't have a response");
					}
				}
			}

			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private final InputStream inputStream = new InputStream() {
		@Override
		public int read() throws IOException {
			if (isClosed) {
				throw new IOException("This is closed!");
			}
			Byte result = outputData.poll();
			return result == null ? -1 : (result & 0xFF);
		}

		@Override
		public int available() {
			return outputData.size();
		}
	};
	private final OutputStream outputStream = new OutputStream() {
		@Override
		public void write(int dataByteInt) throws IOException {
			if (isClosed) {
				throw new IOException("This is closed!");
			}
			byte dataByte = (byte) dataByteInt;
			inputData.add(dataByte);
			lastWriteNanos = System.nanoTime();
		}
	};
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public void close() {
		isClosed = true;
	}
}
