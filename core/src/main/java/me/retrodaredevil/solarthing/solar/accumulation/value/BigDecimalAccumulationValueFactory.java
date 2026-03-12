package me.retrodaredevil.solarthing.solar.accumulation.value;

import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;

@NullMarked
public class BigDecimalAccumulationValueFactory implements AccumulationValueFactory<BigDecimalAccumulationValue> {
	private static final BigDecimalAccumulationValueFactory INSTANCE = new BigDecimalAccumulationValueFactory();
	private static final BigDecimalAccumulationValue ZERO = new BigDecimalAccumulationValue(BigDecimal.ZERO);

	private BigDecimalAccumulationValueFactory() {}
	public static BigDecimalAccumulationValueFactory getInstance() { return INSTANCE; }

	@Override
	public BigDecimalAccumulationValue getZero() {
		return ZERO;
	}
}
