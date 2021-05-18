package me.retrodaredevil.solarthing.solar.accumulation.value;

import me.retrodaredevil.solarthing.solar.accumulation.TotalGetter;

import java.util.function.Function;

public final class FloatAccumulationValue implements AccumulationValue<FloatAccumulationValue> {
	private final float value;

	public FloatAccumulationValue(float value) {
		this.value = value;
	}

	public float getValue() {
		return value;
	}
	public static <T> TotalGetter<T, FloatAccumulationValue> convert(Function<? super T, Float> totalGetter) {
		return packet -> new FloatAccumulationValue(totalGetter.apply(packet));
	}

	@Override
	public FloatAccumulationValue plus(FloatAccumulationValue other) {
		return new FloatAccumulationValue(value + other.value);
	}

	@Override
	public FloatAccumulationValue minus(FloatAccumulationValue other) {
		return new FloatAccumulationValue(value - other.value);
	}
}
