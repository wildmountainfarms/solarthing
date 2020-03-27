package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * An enum that represents different options that can be easily enabled
 */
public enum ExtraOptionFlag {
	RPI_LOG_CPU_TEMPERATURE("rpi_cpu_temp");
	private final String shortName;
	ExtraOptionFlag(String shortName) {
		this.shortName = shortName;
	}
	@JsonValue
	public String getShortName(){
		return shortName;
	}

	@JsonCreator
	public static ExtraOptionFlag forValue(String shortName){
		ExtraOptionFlag r = forValueOrNull(shortName);
		if(r == null){
			throw new IllegalArgumentException("shortName=" + shortName + " is not valid!");
		}
		return r;
	}

	public static ExtraOptionFlag forValueOrNull(String shortName){
		for(ExtraOptionFlag extraOptionFlag : values()){
			if(extraOptionFlag.shortName.equals(shortName)){
				return extraOptionFlag;
			}
		}
		return null;
	}
}
