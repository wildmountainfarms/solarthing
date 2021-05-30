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

	private final Map<Integer, ModbusSlave> addressToSlaveMap;
	private final IODataEncoder ioDataEncoder;

	private final Queue<Byte> outputData = new ConcurrentLinkedDeque<>();
	private final Queue<Byte> inputData = new ConcurrentLinkedDeque<>();
	private transient boolean isClosed = false;
	private transient Long lastWrite = null;

	public DummyModbusIO(Map<Integer, ModbusSlave> addressToSlaveMap, IODataEncoder ioDataEncoder) {
		this.addressToSlaveMap = addressToSlaveMap;
		this.ioDataEncoder = ioDataEncoder;

		Thread thread = new Thread(this::runThread);
		thread.setDaemon(true);
		thread.start();
	}

	private void runThread() {
		while (!Thread.currentThread().isInterrupted() && !isClosed) {
			long now = System.currentTimeMillis();
			Long lastWrite = this.lastWrite;
			if (lastWrite == null || lastWrite + 6 < now) {
				byte[] data = new byte[inputData.size()];
				// RtuDataEncoder requires at least 4 bytes to do something useful, so if we don't have that, just wait
				//   Also note that if data.length is 0, then the current implementation of RtuDataEncoder would hang because the call to available() would stay 0
				if (data.length >= 4) {
					for (int i = 0; i < data.length; i++) {
						int value = requireNonNull(inputData.poll(), "Should not have gotten null! i: " + i + " length: " + data.length);
						data[i] = (byte) value;
					}
					Integer respondingAddress = null;
					ModbusMessage responseMessage = null;
					for (Map.Entry<Integer, ModbusSlave> entry : addressToSlaveMap.entrySet()) {
						int address = entry.getKey();
						ModbusSlave slave = entry.getValue();
						final ModbusMessage requestMessage;
						try {
							// TODO IODataEncoder needs a method that doesn't require the address, but can parse a modbus message along with the present address
							requestMessage = ioDataEncoder.readMessage(address, new ByteArrayInputStream(data));
						} catch (UnexpectedSlaveResponseException ignored) {
							continue;
						} catch (RedundancyException ex) {
							LOGGER.debug("Got redundancy exception", ex);
							break;
						}
						respondingAddress = address;
						responseMessage = slave.sendRequestMessage(requestMessage);
						break;
					}
					if (responseMessage != null) {
						requireNonNull(respondingAddress);
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						ioDataEncoder.sendMessage(outputStream, respondingAddress, responseMessage);
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
			lastWrite = System.currentTimeMillis();
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
