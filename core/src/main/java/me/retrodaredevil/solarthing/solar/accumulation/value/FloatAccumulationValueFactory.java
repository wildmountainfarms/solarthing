package me.retrodaredevil.solarthing.solar.accumulation.value;

public class FloatAccumulationValueFactory implements AccumulationValueFactory<FloatAccumulationValue> {
	private static final FloatAccumulationValueFactory INSTANCE = new FloatAccumulationValueFactory();
	private static final FloatAccumulationValue ZERO = new FloatAccumulationValue(0.0f);

	private FloatAccumulationValueFactory() {}
	public static FloatAccumulationValueFactory getInstance() { return INSTANCE; }

	@Override
	public FloatAccumulationValue getZero() {
		return ZERO;
	}
}
