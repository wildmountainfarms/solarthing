package me.retrodaredevil.action.node.environment;

import me.retrodaredevil.solarthing.solar.tracer.TracerReadTable;
import me.retrodaredevil.solarthing.solar.tracer.TracerWriteTable;

public final class TracerModbusEnvironment {
	private final TracerReadTable read;
	private final TracerWriteTable write;

	public TracerModbusEnvironment(TracerReadTable read, TracerWriteTable write) {
		this.read = read;
		this.write = write;
	}

	public TracerReadTable getRead() {
		return read;
	}

	public TracerWriteTable getWrite() {
		return write;
	}
}
