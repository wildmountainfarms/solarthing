package me.retrodaredevil.solarthing.misc.weather;

import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.misc.source.W1Source;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class W1TemperatureListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(W1TemperatureListUpdater.class);
	private static final float RATED_MIN_TEMP_CELSIUS = -55.0f; // DS18B20 min temp
	private static final float RATED_MAX_TEMP_CELSIUS = 125.0f; // DS18B20 max temp
	/** Allow this many degrees celsius to be read below and above the min and max temperature respectively */
	private static final float LENIENT_TEMP_CELSIUS = 20.0f;

	private final File slaveFile;
	private final File nameFile;

	private final int dataId;

	public W1TemperatureListUpdater(File directory, int dataId) {
		slaveFile = new File(directory, "w1_slave");
		nameFile = new File(directory, "name");
		this.dataId = dataId;

		if (!directory.isDirectory()) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "file: " + directory + " is not a directory! Program will continue. (Maybe it will connect later)");
		}
	}

	@Override
	public void receive(List<Packet> packets) {
		long startTimeNanos = System.nanoTime();
		final String name;
		try {
			name = readContents(nameFile).replaceAll("\n", ""); // remove new line at end
		} catch (FileNotFoundException e){
			LOGGER.error("name file does not exist. file: " + nameFile);
			return;
		} catch (IOException e) {
			LOGGER.debug("Could not read name file", e); // debug because this is so common
			return;
		}
		final List<String> lines;
		try {
			lines = Files.readAllLines(slaveFile.toPath(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOGGER.error("Could not read slave file for name=" + name, e);
			return;
		}
		if (lines.isEmpty()) {
			LOGGER.debug("lines is empty! name=" + name); // debug since this is so common
			return;
		} else if (lines.size() != 2) {
			LOGGER.warn("lines.size() is " + lines.size() + "! lines=" + lines + ". name=" + name);
			return;
		}
		String line1 = lines.get(0);
		String line2 = lines.get(1);

		String[] split1 = line1.split(" ");
		String message = split1[split1.length - 1];
		if (!"YES".equals(message)) {
			LOGGER.debug("Unsuccessful message: " + message + ". name=" + name); // debug since this is so common
			return;
		}
		String[] split2 = line2.split("=");
		String temperatureString = split2[split2.length - 1];
		final int temperatureRaw;
		try {
			temperatureRaw = Integer.parseInt(temperatureString);
		} catch (NumberFormatException ex) {
			LOGGER.warn("Unable to parse. line2=" + line2 + " name=" + name, ex);
			return;
		}
		// Why 25C? Sometimes this just happens, so I'm debugging it to see how common it is.
		if (temperatureRaw == 25000) {
			LOGGER.debug("Temperature is exactly 25C. line1: " + line1);
		}
		if (line1.contains("crc=00")) {
			LOGGER.debug("CRC start with 0! Maybe bad? line1: " + line1 + " raw temp: " + temperatureRaw);
		}
		if (temperatureRaw == 0 && line1.contains("crc=00")
				|| temperatureRaw == 25000 && (line1.contains("crc=4e") || line1.contains("crc=b4"))) { // If 0C and crc=0, then bad reading. Also bad reading if 25C and crc=4e or crc=b4
			LOGGER.debug("crc=00! name=" + name + " line1: " + line1 + " line2: " + line2); // debug since this is so common
			return;
		}
		long timeTakenNanos = System.nanoTime() - startTimeNanos;
		float temperatureCelsius = temperatureRaw / 1000.0f;
		if (temperatureCelsius < RATED_MIN_TEMP_CELSIUS - LENIENT_TEMP_CELSIUS) {
			LOGGER.warn("Extremely low temp! Probably not accurate! temperatureCelsius: " + temperatureCelsius);
			return;
		}
		if (temperatureCelsius > RATED_MAX_TEMP_CELSIUS + LENIENT_TEMP_CELSIUS) {
			LOGGER.warn("Extremely high temp! Probably not accurate! temperatureCelsius: " + temperatureCelsius);
			return;
		}
		packets.add(new CelsiusTemperaturePacket(dataId, new W1Source(name), temperatureCelsius));
		LOGGER.debug("Read temperature " + temperatureCelsius + "C from " + name + " in " + TimeUtil.nanosToSecondsString(timeTakenNanos) + " seconds");
	}
	private static String readContents(File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
	}
}
