package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.program.ConfigUtil;
import me.retrodaredevil.solarthing.program.RoverMain;
import me.retrodaredevil.solarthing.solar.batteryvoltage.BatteryVoltageIOListUpdater;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;

import java.io.File;

@JsonTypeName("battery-voltage-io")
@JsonExplicit
public class BatteryVoltageIODataRequester implements DataRequester {
	private final File ioBundleFile;
	private final int dataId;
	private final double multiplier;
	private final double invalidWhenBelow;
	private final double invalidWhenAbove;

	@JsonCreator
	public BatteryVoltageIODataRequester(
			@JsonProperty(value = "io", required = true) File ioBundleFile,
			@JsonProperty(value = "data_id", required = true) int dataId,
			@JsonProperty("multiplier") Double multiplier,
			@JsonProperty("divisor") Double divisor,
			@JsonProperty(value = "invalid_when_below") Double invalidWhenBelow,
			@JsonProperty(value = "invalid_when_above") Double invalidWhenAbove
	) {
		this.ioBundleFile = ioBundleFile;
		this.dataId = dataId;
		this.multiplier = (multiplier == null ? 1 : multiplier) / (divisor == null ? 1 : divisor);
		this.invalidWhenBelow = invalidWhenBelow == null ? 0 : invalidWhenBelow;
		this.invalidWhenAbove = invalidWhenAbove == null ? Double.MAX_VALUE : invalidWhenAbove;
	}

	@Override
	public PacketListReceiver createPacketListReceiver(RequestObject requestObject) {
		final IOBundle ioBundle = ConfigUtil.createIOBundle(ioBundleFile, RoverReadTable.SERIAL_CONFIG);

		return new BatteryVoltageIOListUpdater(ioBundle.getInputStream(), dataId, multiplier, invalidWhenBelow, invalidWhenAbove);
	}
}
