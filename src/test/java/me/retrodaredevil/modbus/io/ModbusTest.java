package me.retrodaredevil.modbus.io;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ModbusTest {
	@Test
	void testLRC(){
		int[] data = new int[] { 17, 3, 0, 107, 0, 3 };
		assertEquals(0x7E, RedundancyUtil.calculateLRC(data));
		
		int sum = 0;
		for(int a : data){
			sum += a;
		}
		sum += 0x7E;
		assertEquals(0, sum & 0xFF);
	}
	@Test
	void testCRC(){
		int[] data = { 0x01, 0x06, 0xE0, 0x1D, 0x00, 0x08};
		
		assertEquals(0x2FCA, RedundancyUtil.flipCRC(RedundancyUtil.calculateCRC(data)));
	}
}
