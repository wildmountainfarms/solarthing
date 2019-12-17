package me.retrodaredevil.solarthing.pvoutput.data;

import me.retrodaredevil.solarthing.pvoutput.PVOutputString;

public enum WeatherCondition implements PVOutputString {
	FINE("Fine"),
	PARTLY_CLOUDY("Partly Cloudy"),
	MOSTLY_CLOUDY("Mostly Cloudy"),
	CLOUDY("Cloudy"),
	SHOWERS("Showers"),
	SNOW("Snow"),
	NOT_SURE("Not Sure")
	;
	private final String name;

	WeatherCondition(String name) {
		this.name = name;
	}

	@Override
	public String toPVOutputString() {
		return name;
	}

	public String getName(){
		return name;
	}
	public static WeatherCondition getConditionFromString(String string){
		for(WeatherCondition condition : values()){
			if(condition.name.equals(string)){
				return condition;
			}
		}
		return null;
	}
}
