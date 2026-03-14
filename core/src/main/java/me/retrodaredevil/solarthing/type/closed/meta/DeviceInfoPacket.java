package me.retrodaredevil.solarthing.type.closed.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@JsonTypeName("DEVICE_INFO")
@JsonExplicit
@NullMarked
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
	public @NonNull TargetedMetaPacketType getPacketType() {
		return TargetedMetaPacketType.DEVICE_INFO;
	}

	@JsonProperty("deviceName")
	public @NonNull String getDeviceName() {
		return deviceName;
	}

	@JsonProperty("deviceDescription")
	public @NonNull String getDeviceDescription() {
		return deviceDescription;
	}

	@JsonProperty("deviceLocation")
	public @NonNull String getDeviceLocation() {
		return deviceLocation;
	}
}
