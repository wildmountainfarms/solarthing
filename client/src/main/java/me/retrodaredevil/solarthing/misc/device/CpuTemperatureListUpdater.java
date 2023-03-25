package me.retrodaredevil.solarthing.misc.device;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CpuTemperatureListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(CpuTemperatureListUpdater.class);

	private final int processorCount;

	public CpuTemperatureListUpdater(int processorCount) {
		this.processorCount = processorCount;
	}

	@Override
	public void receive(List<Packet> packets) {
		List<CpuTemperaturePacket.Core> cores = new ArrayList<>();
		int totalMilliCelsius = 0;
		for (int number = 0; number < processorCount; number++) {
			Path path = Paths.get("/sys/class/thermal/thermal_zone" + number + "/temp");
			final String contents;
			try {
				contents = Files.readString(path, StandardCharsets.UTF_8).stripTrailing();
			} catch (IOException e) {
				LOGGER.error("Could not read path: " + path, e);
				continue;
			}
			final int milliCelsius;
			try {
				milliCelsius = Integer.parseInt(contents);
			} catch (NumberFormatException e) {
				LOGGER.warn("Could not parse contents: " + contents);
				continue;
			}
			totalMilliCelsius += milliCelsius;
			float celsius = milliCelsius / 1000.0f;
			cores.add(new CelsiusCpuTemperaturePacket.CelsiusCore(number, celsius));
		}
		if (cores.isEmpty()) {
			LOGGER.warn("Could not read any CPU temperatures!");
			return;
		}
		float averageCelsius = totalMilliCelsius / (1000.0f * cores.size());

		packets.add(new CelsiusCpuTemperaturePacket(
				CpuTemperaturePacket.VERSION_WITH_CORES,
				averageCelsius,
				cores
		));
	}
}
