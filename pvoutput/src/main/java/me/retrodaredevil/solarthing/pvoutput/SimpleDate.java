package me.retrodaredevil.solarthing.pvoutput;

public final class SimpleDate implements PVOutputString {
	private final int year;
	private final int month;
	private final int day;

	public SimpleDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
		if(month < 0 || month > 12){
			throw new IllegalArgumentException("Month is out of range! month=" + month);
		}
		if(day < 0 || day > 31){
			throw new IllegalArgumentException("Day is out of range! day=" + day);
		}
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}
	@Override
	public String toPVOutputString(){
		if(year < 0){
			throw new IllegalStateException("Year is < 0! year=" + year);
		}
		if(year > 9999){
			throw new IllegalStateException("Year is > 9999! year=" + year);
		}
		StringBuilder yearString = new StringBuilder("" + year);
		while(yearString.length() < 4){
			yearString.insert(0, "0");
		}
		String monthString = "" + month;
		if(monthString.length() < 2){
			monthString = "0" + monthString;
		}
		String dayString = "" + day;
		if(dayString.length() < 2){
			dayString = "0" + dayString;
		}

		return yearString + monthString + dayString;
	}
}
