package me.retrodaredevil.solarthing.config.request.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.modbus.IOModbusSlaveBus;
import me.retrodaredevil.io.modbus.ImmutableAddressModbusSlave;
import me.retrodaredevil.io.modbus.ModbusSlave;
import me.retrodaredevil.io.modbus.ModbusSlaveBus;
import me.retrodaredevil.io.modbus.RtuDataEncoder;
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

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

@JsonTypeName("modbus")
public class ModbusDataRequester implements DataRequester {
	private final Path ioBundleFile;
	private final Map<Integer, ModbusRequester> addressToModbusRequesterMap;
	private final long rtuInitialTimeoutMillis;
	private final long rtuEndTimeoutMillis;

	@JsonCreator
	public ModbusDataRequester(
			@JsonProperty(value = "io", required = true) Path ioBundleFile,
			@JsonProperty(value = "devices", required = true) Map<Integer, ModbusRequester> addressToModbusRequesterMap,
			@JsonProperty("initial_timeout") String initialTimeoutDurationString,
			@JsonProperty("end_timeout") String endTimeoutDurationString
	) {
		this.ioBundleFile = requireNonNull(ioBundleFile);
		this.addressToModbusRequesterMap = requireNonNull(addressToModbusRequesterMap);
		rtuInitialTimeoutMillis = initialTimeoutDurationString == null ? 2000 : Duration.parse(initialTimeoutDurationString).toMillis();
		rtuEndTimeoutMillis = endTimeoutDurationString == null ? 40 : Duration.parse(endTimeoutDurationString).toMillis();
	}

	@Override
	public DataRequesterResult create(RequestObject requestObject) {
		ModbusRequester first = addressToModbusRequesterMap.values().stream().findFirst().orElseThrow(NoSuchElementException::new);
		IOConfig ioConfig = ConfigUtil.parseIOConfig(ioBundleFile, first.getDefaultSerialConfig());
		ReloadableIOBundle ioBundle = new ReloadableIOBundle(ioConfig::createIOBundle);
		ModbusSlaveBus modbus = new IOModbusSlaveBus(ioBundle, new RtuDataEncoder(rtuInitialTimeoutMillis, rtuEndTimeoutMillis, 4));

		ReloadIOSuccessReporterHandler reloadIOSuccessReporterHandler = new ReloadIOSuccessReporterHandler(ioBundle::reload);

		List<PacketListReceiver> packetListReceiverList = new ArrayList<>();
		List<EnvironmentUpdater> environmentUpdaterList = new ArrayList<>();
		for (Map.Entry<Integer, ModbusRequester> entry : addressToModbusRequesterMap.entrySet()) {
			int address = entry.getKey();
			ModbusRequester modbusRequester = entry.getValue();
			ModbusSlave slave = new ImmutableAddressModbusSlave(address, modbus);
			DataRequesterResult result = modbusRequester.create(requestObject, reloadIOSuccessReporterHandler.createReporter(), slave);
			packetListReceiverList.add(result.getStatusPacketListReceiver());
			environmentUpdaterList.add(result.getEnvironmentUpdater());
		}
		return DataRequesterResult.builder()
				.statusPacketListReceiver(new PacketListReceiverMultiplexer(packetListReceiverList))
				.environmentUpdater(new EnvironmentUpdaterMultiplexer(environmentUpdaterList))
				.build();
	}
}
