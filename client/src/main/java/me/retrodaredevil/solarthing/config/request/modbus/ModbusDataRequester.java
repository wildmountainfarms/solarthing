package me.retrodaredevil.solarthing.config.request.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.io.modbus.*;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdaterMultiplexer;
import me.retrodaredevil.solarthing.config.io.IOConfig;
import me.retrodaredevil.solarthing.config.request.DataRequester;
import me.retrodaredevil.solarthing.config.request.DataRequesterResult;
import me.retrodaredevil.solarthing.config.request.RequestObject;
import me.retrodaredevil.solarthing.io.ReloadableIOBundle;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiverMultiplexer;
import me.retrodaredevil.solarthing.program.ConfigUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
	public DataRequesterResult create(RequestObject requestObject) {
		ModbusRequester first = addressToModbusRequesterMap.values().stream().findFirst().orElseThrow(NoSuchElementException::new);
		IOConfig ioConfig = ConfigUtil.parseIOConfig(ioBundleFile, first.getDefaultSerialConfig());
		ReloadableIOBundle ioBundle = new ReloadableIOBundle(ioConfig::createIOBundle);
		ModbusSlaveBus modbus = new IOModbusSlaveBus(ioBundle, new RtuDataEncoder(2000, 20, 4));

		List<PacketListReceiver> packetListReceiverList = new ArrayList<>();
		List<EnvironmentUpdater> environmentUpdaterList = new ArrayList<>();
		for (Map.Entry<Integer, ModbusRequester> entry : addressToModbusRequesterMap.entrySet()) {
			int address = entry.getKey();
			ModbusRequester modbusRequester = entry.getValue();
			ModbusSlave slave = new ImmutableAddressModbusSlave(address, modbus);
			DataRequesterResult result = modbusRequester.create(requestObject, slave);
			packetListReceiverList.add(result.getStatusPacketListReceiver());
			environmentUpdaterList.add(result.getEnvironmentUpdater());
		}
		return new DataRequesterResult(
				new PacketListReceiverMultiplexer(packetListReceiverList),
				new EnvironmentUpdaterMultiplexer(environmentUpdaterList)
		);
	}
}
