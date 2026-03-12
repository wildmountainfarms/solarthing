package me.retrodaredevil.solarthing.solar.accumulation.value;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AccumulationValueFactory<T extends AccumulationValue<T>> {
	T getZero();
}
