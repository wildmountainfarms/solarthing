package me.retrodaredevil.solarthing.solar.renogy.rover.special;

public class ImmutableSpecialPowerControl_E02D implements SpecialPowerControl_E02D{

	private final int upper;
	private final int lower;

	public ImmutableSpecialPowerControl_E02D(int upper, int lower){
		this.upper = upper;
		this.lower = lower;
	}
	public ImmutableSpecialPowerControl_E02D(int value){
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
