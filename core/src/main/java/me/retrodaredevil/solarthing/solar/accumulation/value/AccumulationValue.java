package me.retrodaredevil.solarthing.solar.accumulation.value;

public interface AccumulationValue<T extends AccumulationValue<T>> {

	T plus(T other);
	T minus(T other);
}
