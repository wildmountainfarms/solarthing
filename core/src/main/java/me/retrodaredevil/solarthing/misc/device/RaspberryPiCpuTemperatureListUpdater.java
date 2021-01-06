package me.retrodaredevil.solarthing.misc.device;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RaspberryPiCpuTemperatureListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(RaspberryPiCpuTemperatureListUpdater.class);
	private final byte[] buffer = new byte[1024];
	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		ProcessBuilder processBuilder = new ProcessBuilder("vcgencmd", "measure_temp");
		processBuilder.redirectErrorStream(true);
		try {
			Process process = processBuilder.start();
			boolean finished = process.waitFor(300, TimeUnit.MILLISECONDS);
			if(!finished){
				LOGGER.warn("vcgencmd must have taken longer than 300ms!");
			} else {
				InputStream inputStream = process.getInputStream();
				int len = inputStream.read(buffer);
				if (len < 0) {
					LOGGER.warn("Read length is " + len + "!");
					return;
				}
				String result = new String(buffer, 0, len);
				String[] split1 = result.split("=");
				if(split1.length != 2){
					LOGGER.warn("split1.length != 2!!! split1.length=" + split1.length);
					return;
				}
				String[] split2 = split1[1].split("'");
				if(split2.length != 2){
					LOGGER.warn("split2.length != 2!!! split2.length=" + split2.length);
					return;
				}
				String toParse = split2[0];
				try {
					float resultCelsius = Float.parseFloat(toParse);
					packets.add(new CelsiusCpuTemperaturePacket(resultCelsius));
				} catch (NumberFormatException e){
					LOGGER.warn("Couldn't parse! toParse=" + toParse + ". Result=" + result, e);
				}
			}
			process.destroy();
			process.waitFor(50, TimeUnit.MILLISECONDS);
			if (process.isAlive()) {
				process.destroyForcibly().waitFor(50, TimeUnit.MILLISECONDS);
				LOGGER.warn("Had to destroy forcibly! is still alive: " + process.isAlive());
			}
		} catch (IOException | InterruptedException e) {
			LOGGER.error("Error while running command to get CPU temperature", e);
		}
	}
}
