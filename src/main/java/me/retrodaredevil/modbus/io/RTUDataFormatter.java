package me.retrodaredevil.modbus.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RTUDataFormatter implements IODataFormatter {
	private final long initialTimeout;
	private final long endMillis;
	public RTUDataFormatter(long initialTimeout, long endMillis){
		this.initialTimeout = initialTimeout;
		this.endMillis = endMillis;
	}
	public RTUDataFormatter(){
		this(1000, 10);
	}
	@Override
	public void sendMessage(OutputStream outputStream, int address, ModbusMessage message) {
		byte code = message.getByteFunctionCode();
		byte[] data = message.getByteData();
		int crc = RedundancyUtil.calculateCRC(data);
		byte highCrc = (byte) ((crc & (0xFF << 8)) >> 8);
		byte lowCrc = (byte) (crc & 0xFF);
		byte[] bytes = new byte[4 + data.length];
		bytes[0] = (byte) address;
		bytes[1] = code;
		for(int i = 0; i < data.length; i++){
			bytes[i + 2] = data[i];
		}
		bytes[data.length + 2] = lowCrc;
		bytes[data.length + 3] = highCrc;
		try {
			outputStream.write(bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ModbusMessage readMessage(int expectedAddress, InputStream inputStream) {
		final byte[] bytes;
		try {
			bytes = readBytes(inputStream);
		} catch(IOException e){
			throw new RuntimeException(e);
		}
		int length = bytes.length;
		int address = bytes[0];
		byte code = bytes[1];
		byte[] data = new byte[length - 4];
		System.arraycopy(bytes, 2, data, 0, length - 4);
		int lowCrc = bytes[length - 2];
		int highCrc = bytes[length - 1];
		if(address != expectedAddress){
			throw new IllegalStateException("address: " + expectedAddress + " was expected! address: " + address + " responded!"); // replace with custom exception
		}
		int expectedCrc = (highCrc << 8) | lowCrc;
		int actualCrc = RedundancyUtil.calculateCRC(data);
		if(expectedCrc != actualCrc){
			throw new IllegalStateException("Incorrect CRC checksum! expected: " + expectedCrc + " data was: " + actualCrc);
		}
		
		return ModbusMessages.createMessage(code, data);
	}
	private byte[] readBytes(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		List<Byte> bytes = new ArrayList<>();
		boolean started = false;
		long startTime = System.currentTimeMillis();
		while(true){
			if(inputStream.available() != 0){
				int len = inputStream.read(buffer);
				if(len <= 0){
					throw new AssertionError("We check InputStream#available()! len should not be <= 0! It's: " + len);
				}
				started = true;
				for(int i = 0; i < len; i++){
					bytes.add(buffer[i]);
				}
			} else {
				long currentTime = System.currentTimeMillis();
				if(started){
					if(startTime + initialTimeout < currentTime){
						throw new IllegalStateException("Timed out!"); // TODO replace this with a custom exception
					}
				} else {
					if(startTime + endMillis < currentTime){
						int size = bytes.size();
						byte[] r = new byte[size];
						for(int i = 0; i < size; i++){
							r[i] = bytes.get(i);
						}
						return r;
					}
				}
			}
		}
	}
}
