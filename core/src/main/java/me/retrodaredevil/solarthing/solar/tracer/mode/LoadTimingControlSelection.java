package me.retrodaredevil.solarthing.solar.tracer.mode;

import me.retrodaredevil.solarthing.packets.CodeMode;

public enum LoadTimingControlSelection implements CodeMode {
	ONE_TIMER(0, "One timer"),
	TWO_TIMER(1, "Two timer")
	;
	private final int value;
	private final String name;

	LoadTimingControlSelection(int value, String name) {
		this.value = value;
		this.name = name;
	}

	@Override
	public int getValueCode() {
		return value;
	}

	@Override
	public String getModeName() {
		return name;
	}
}
