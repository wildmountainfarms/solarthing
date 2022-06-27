package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.annotations.Nullable;

public enum WeatherCondition implements PVOutputString {
	FINE("Fine"),
	PARTLY_CLOUDY("Partly Cloudy"),
	MOSTLY_CLOUDY("Mostly Cloudy"),
	CLOUDY("Cloudy"),
	SHOWERS("Showers"),
	SNOW("Snow"),
	HAZY("Hazy"),
	FOG("Fog"),
	DUSTY("Dusty"),
	FROST("Frost"),
	STORM("Storm"),
	NOT_SURE("Not Sure"),
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
	public static @Nullable WeatherCondition getConditionFromStringOrNull(String string){
		for(WeatherCondition condition : values()){
			if(condition.name.equals(string)){
				return condition;
			}
		}
		return null;
	}
}
