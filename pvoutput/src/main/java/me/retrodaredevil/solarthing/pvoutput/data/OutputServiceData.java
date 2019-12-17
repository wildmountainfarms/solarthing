package me.retrodaredevil.solarthing.pvoutput.data;

import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;

public interface OutputServiceData {
	SimpleDate getOutputDate();

	/**
	 * Normally this should not be null. This is required if the output does not already exist
	 * @return The number of generated watt hours, or null
	 */
	Number getGenerated();
	/** @return The number of exported watt hours*/
	Number getExported();
	/** @return The peak power in watts*/
	Number getPeakPower();
	SimpleTime getPeakTime();

	String getConditionValue();
	default WeatherCondition getCondition(){
		String value = getConditionValue();
		if(value == null){
			return null;
		}
		return WeatherCondition.getConditionFromString(value);
	}

	Float getMinimumTemperatureCelsius();
	Float getMaximumTemperatureCelsius();

	String getComments();

	Number getImportPeak();
	Number getImportOffPeak();
	Number getImportShoulder();
	Number getImportHighShoulder();
	Number getConsumption();
}
