package me.retrodaredevil.solarthing.solar.renogy;

public final class Version {
	private final int high, middle, low;
	
	public Version(int versionCodeRaw) {
		int versionCode = versionCodeRaw & 0x00FFFFFF;
		low = versionCode & 0xFF;
		middle = (versionCode & 0xFF00) >>> 16;
		high = (versionCode & 0xFF0000) >>> 24;
	}
	public int getMajor(){
		return high;
	}
	public int getMinor(){
		return middle;
	}
	public int getPatch(){
		return low;
	}
	
	
	private static String twoPlaces(int number){
		if(number > 100){
			throw new IllegalArgumentException("number cannot be greater than 100! it was: " + number);
		}
		if(number < 0){
			throw new IllegalArgumentException("number cannot be less than 0! it was: " + number);
		}
		String r = "" + number;
		switch(r.length()){
			case 2: return r;
			case 1: return "0" + r;
			default: throw new AssertionError();
		}
	}
	
	@Override
	public String toString() {
		return "V" + twoPlaces(high) + "." + twoPlaces(middle) + "." +twoPlaces(low);
	}
}
