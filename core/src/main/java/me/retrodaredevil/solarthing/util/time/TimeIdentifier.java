package me.retrodaredevil.solarthing.util.time;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TimeIdentifier {
	long getTimeId(long timeMillis);
}
