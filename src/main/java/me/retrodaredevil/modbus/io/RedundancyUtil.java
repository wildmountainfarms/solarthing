package me.retrodaredevil.modbus.io;

public final class RedundancyUtil {
	private RedundancyUtil(){ throw new UnsupportedOperationException(); }
	
	public static int calculateLRC(int[] bytes){
		int sum = 0;
		for(int a : bytes){
			sum += a;
		}
		return -((byte) (sum & 0xFF));
	}
	public static int calculateCRC(int[] bytes){
		int crc = 0xFFFF;
		for(int b : bytes){
			crc ^= b;
			for(int i = 8; i > 0; i--){
				if((crc & 1) != 0){
					crc >>= 1;
					crc ^= 0xA001;
				} else {
					crc >>= 1;
				}
			}
		}
		return crc;
	}
	public static int calculateCRC(byte[] bytes){
		int crc = 0xFFFF;
		for(int b : bytes){
			crc ^= b;
			for(int i = 8; i > 0; i--){
				if((crc & 1) != 0){
					crc >>= 1;
					crc ^= 0xA001;
				} else {
					crc >>= 1;
				}
			}
		}
		return crc;
	}
	public static int flipCRC(int crc){
		int high = (crc & (0xFF << 8)) >> 8;
		int low = crc & 0xFF;
		return (low << 8) | high;
	}
	
}
