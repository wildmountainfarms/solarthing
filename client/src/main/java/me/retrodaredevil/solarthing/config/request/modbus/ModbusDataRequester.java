package me.retrodaredevil.solarthing.config.request.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.solarthing.config.request.DataRequester;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.program.ConfigUtil;

import java.io.File;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

public class ModbusDataRequester implements DataRequester {
	private final File ioBundleFile;
	private final Map<Integer, ModbusRequester> addressToModbusRequesterMap;

	@JsonCreator
	public ModbusDataRequester(
			@JsonProperty(value = "io", required = true) File ioBundleFile,
			@JsonProperty(value = "devices", required = true) Map<Integer, ModbusRequester> addressToModbusRequesterMap) {
		requireNonNull(this.ioBundleFile = ioBundleFile);
		requireNonNull(this.addressToModbusRequesterMap = addressToModbusRequesterMap);
	}

	@Override
	public PacketListReceiver createPacketListReceiver(PacketListReceiver eventPacketReceiver) {
		ModbusRequester first = addressToModbusRequesterMap.values().stream().findFirst().orElseThrow(NoSuchElementException::new);
		final IOBundle ioBundle = ConfigUtil.createIOBundle(ioBundleFile, first.getDefaultSerialConfig());
		return null;
	}
}
