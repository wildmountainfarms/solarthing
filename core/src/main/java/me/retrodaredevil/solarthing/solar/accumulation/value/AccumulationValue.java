package me.retrodaredevil.solarthing.solar.accumulation.value;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AccumulationValue<T extends AccumulationValue<T>> {

	T plus(T other);
	T minus(T other);
}
