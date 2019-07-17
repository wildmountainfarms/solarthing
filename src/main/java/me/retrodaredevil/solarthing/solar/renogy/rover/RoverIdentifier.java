package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.identification.Identifier;

import java.util.Objects;

public final class RoverIdentifier implements Identifier {
	private final int hardwareVersion;
	private final int serialNumber;
	private final int controllerDeviceAddress;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RoverIdentifier that = (RoverIdentifier) o;
		return hardwareVersion == that.hardwareVersion &&
			serialNumber == that.serialNumber &&
			controllerDeviceAddress == that.controllerDeviceAddress;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(hardwareVersion, serialNumber, controllerDeviceAddress);
	}
	
	public RoverIdentifier(int hardwareVersion, int serialNumber, int controllerDeviceAddress) {
		this.hardwareVersion = hardwareVersion;
		this.serialNumber = serialNumber;
		this.controllerDeviceAddress = controllerDeviceAddress;
	}
}
