package me.retrodaredevil.solarthing.util.time;

import org.jspecify.annotations.NullMarked;

import java.time.Instant;

@NullMarked
public interface TimeIdentifier {
	long getTimeId(Instant time);
}
