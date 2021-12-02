package me.retrodaredevil.action.node.util;

@FunctionalInterface
public interface NanoTimeProvider {
	long getNanos();

	NanoTimeProvider SYSTEM_NANO_TIME = System::nanoTime;
}
