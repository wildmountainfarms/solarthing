package me.retrodaredevil.solarthing.type.closed.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonTypeName("DEVICE_INFO")
@JsonExplicit
public final class DeviceInfoPacket implements TargetedMetaPacket {
	private final String deviceName;
	private final String deviceDescription;
	private final String deviceLocation;

	@JsonCreator
	public DeviceInfoPacket(
			@JsonProperty(value = "deviceName", required = true) String deviceName,
			@JsonProperty(value = "deviceDescription", required = true) String deviceDescription,
			@JsonProperty(value = "deviceLocation", required = true) String deviceLocation) {
		this.deviceName = deviceName;
		this.deviceDescription = deviceDescription;
		this.deviceLocation = deviceLocation;
	}

	@Override
	public @NotNull TargetedMetaPacketType getPacketType() {
		return TargetedMetaPacketType.DEVICE_INFO;
	}

	@JsonProperty("deviceName")
	public @NotNull String getDeviceName() {
		return deviceName;
	}

	@JsonProperty("deviceDescription")
	public @NotNull String getDeviceDescription() {
		return deviceDescription;
	}

	@JsonProperty("deviceLocation")
	public @NotNull String getDeviceLocation() {
		return deviceLocation;
	}
}
