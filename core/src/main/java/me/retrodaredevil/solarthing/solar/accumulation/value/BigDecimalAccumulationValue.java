package me.retrodaredevil.solarthing.solar.accumulation.value;

import me.retrodaredevil.solarthing.solar.accumulation.TotalGetter;

import java.math.BigDecimal;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class BigDecimalAccumulationValue implements AccumulationValue<BigDecimalAccumulationValue> {
	private final BigDecimal value;

	public BigDecimalAccumulationValue(BigDecimal value) {
		requireNonNull(this.value = value);
	}
	public static <T> TotalGetter<T, BigDecimalAccumulationValue> convert(Function<T, BigDecimal> totalGetter) {
		return packet -> new BigDecimalAccumulationValue(totalGetter.apply(packet));
	}
	public static <T> TotalGetter<T, BigDecimalAccumulationValue> convert(TotalGetter<T, FloatAccumulationValue> totalGetter) {
		return packet -> new BigDecimalAccumulationValue(totalGetter.getTotal(packet).toBigDecimal());
	}

	public BigDecimal getValue() {
		return value;
	}

	@Override
	public BigDecimalAccumulationValue plus(BigDecimalAccumulationValue other) {
		return new BigDecimalAccumulationValue(value.add(other.value));
	}

	@Override
	public BigDecimalAccumulationValue minus(BigDecimalAccumulationValue other) {
		return new BigDecimalAccumulationValue(value.subtract(other.value));
	}
}
