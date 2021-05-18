package me.retrodaredevil.solarthing.solar.accumulation.value;

public interface AccumulationValueFactory<T extends AccumulationValue<T>> {
	T getZero();
}
