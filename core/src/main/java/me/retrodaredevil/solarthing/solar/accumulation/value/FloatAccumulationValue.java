package me.retrodaredevil.solarthing.solar.accumulation.value;

import me.retrodaredevil.solarthing.solar.accumulation.TotalGetter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;

public final class FloatAccumulationValue implements AccumulationValue<FloatAccumulationValue> {
	private final float value;

	public FloatAccumulationValue(float value) {
		this.value = value;
		if (!Float.isFinite(value)) {
			throw new IllegalArgumentException("value must be finite! value: " + value);
		}
	}

	public static <T> TotalGetter<T, FloatAccumulationValue> convert(Function<T, Float> totalGetter) {
		return packet -> new FloatAccumulationValue(totalGetter.apply(packet));
	}
	public float getValue() {
		return value;
	}
	public BigDecimal toBigDecimal() {
		// floats can store about 7 digits of precision, so we can go with 7 digits of prevision.
		//   This means that if we convert to a BigDecimal, we might actually lose some information, but that's the point.
		//   We want to round it to what we think it should be
		return new BigDecimal(value, new MathContext(7, RoundingMode.HALF_EVEN));
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
