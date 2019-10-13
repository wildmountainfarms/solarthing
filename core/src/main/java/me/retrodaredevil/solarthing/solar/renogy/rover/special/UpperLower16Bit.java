package me.retrodaredevil.solarthing.solar.renogy.rover.special;

import static me.retrodaredevil.util.NumberUtil.checkRange;

public interface UpperLower16Bit {
	int getUpper();
	int getLower();
	
	default int getCombined(){
		return getCombined(getUpper(), getLower());
	}
	default short getCombinedShort(){
		return (short) getCombined();
	}
	static int getCombined(int upper, int lower){
		checkRange(0, 255, upper);
		checkRange(0, 255, lower);
		return (upper << 8) | lower;
	}
}
