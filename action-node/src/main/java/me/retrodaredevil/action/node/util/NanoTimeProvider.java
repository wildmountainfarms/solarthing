package me.retrodaredevil.action.node.util;

import org.jspecify.annotations.NullMarked;

@FunctionalInterface
@NullMarked
public interface NanoTimeProvider {
	long getNanos();

	NanoTimeProvider SYSTEM_NANO_TIME = System::nanoTime;
}
