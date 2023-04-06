package me.retrodaredevil.solarthing.solar.renogy.rover.special;

public class ImmutableSpecialPowerControl_E021 implements SpecialPowerControl_E021 {

	private final int upper;
	private final int lower;

	public ImmutableSpecialPowerControl_E021(int upper, int lower){
		this.upper = upper;
		this.lower = lower;
	}
	public ImmutableSpecialPowerControl_E021(int value){
		this(value >>> 8, value & 0xFF);
	}

	@Override
	public int getUpper() {
		return upper;
	}

	@Override
	public int getLower() {
		return lower;
	}
}
