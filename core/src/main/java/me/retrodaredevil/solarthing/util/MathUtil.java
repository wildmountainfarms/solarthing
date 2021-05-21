package me.retrodaredevil.solarthing.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtil {

	private static final MathContext FLOAT_CONTEXT = new MathContext(6, RoundingMode.HALF_UP);

	public static BigDecimal roundFloatToBigDecimal(float value) {
		// floats can store about 7 digits of precision, so we can go with 6 digits of prevision to be safe.
		//   This means that if we convert to a BigDecimal, we might actually lose some information, but that's the point.
		//   We want to round it to what we think it should be
		return new BigDecimal(value, FLOAT_CONTEXT);
	}
	public static float roundFloat(float value) {
		return roundFloatToBigDecimal(value).floatValue();
	}
}
