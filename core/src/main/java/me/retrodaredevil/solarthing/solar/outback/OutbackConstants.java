package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.io.serial.SerialConfig;
import me.retrodaredevil.io.serial.SerialConfigBuilder;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class OutbackConstants {
	private OutbackConstants(){ throw new UnsupportedOperationException(); }

	public static final SerialConfig MATE_CONFIG = new SerialConfigBuilder(19200)
			.setDataBits(8)
			.setParity(SerialConfig.Parity.NONE)
			.setStopBits(SerialConfig.StopBits.ONE)
			.setDTR(true)
			.build();
}
