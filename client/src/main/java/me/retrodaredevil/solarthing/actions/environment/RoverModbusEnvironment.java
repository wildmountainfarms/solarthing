package me.retrodaredevil.action.node.environment;

import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;

public class RoverModbusEnvironment {
	private final RoverReadTable read;
	private final RoverWriteTable write;

	public RoverModbusEnvironment(RoverReadTable read, RoverWriteTable write) {
		this.read = read;
		this.write = write;
	}

	public RoverReadTable getRead() {
		return read;
	}

	public RoverWriteTable getWrite() {
		return write;
	}
}
