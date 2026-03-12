package me.retrodaredevil.solarthing.solar.accumulation;

import me.retrodaredevil.solarthing.solar.accumulation.value.AccumulationValue;
import org.jspecify.annotations.NullMarked;

@FunctionalInterface
@NullMarked
public interface TotalGetter<T, U extends AccumulationValue<U>> {
	U getTotal(T t);
}
