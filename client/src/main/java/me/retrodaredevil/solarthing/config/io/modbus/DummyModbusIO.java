package me.retrodaredevil.solarthing.config.io.modbus;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.io.modbus.IODataEncoder;
import me.retrodaredevil.io.modbus.ModbusMessage;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.UnexpectedSlaveResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		thread.start();
	}



	private void runThread() {
		while (!Thread.currentThread().isInterrupted() && !isClosed) {
			long now = System.currentTimeMillis();
			Long lastWrite = this.lastWrite;
			if (lastWrite == null || lastWrite + 30 < now) {
				byte[] data = new byte[inputData.size()];
				if (data.length > 0) { // this is a necessary check because the current RtuIODataEncoder implementation hangs when given 0 bytes
					for (int i = 0; i < data.length; i++) {
						int value = requireNonNull(inputData.poll(), "Should not have gotten null! i: " + i + " length: " + data.length);
						data[i] = (byte) value;
					}
					LOGGER.debug("Got data: " + Arrays.toString(data));
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
						LOGGER.debug("Responded with: " + Arrays.toString(responseBytes));
					} else {
						LOGGER.debug("Didn't have a response");
					}
				}
			}

			try {
				Thread.sleep(10);
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
			LOGGER.debug("Polled result: " + result);
			return result == null ? -1 : (result & 0xFF);
		}

		@Override
		public int available() throws IOException {
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
			System.out.println("Wrote: " + dataByte);
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
