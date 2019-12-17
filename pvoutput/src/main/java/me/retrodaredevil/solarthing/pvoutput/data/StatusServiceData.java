package me.retrodaredevil.solarthing.pvoutput.data;

import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;

/**
 * @see <a href="https://pvoutput.org/help.html#api-addstatus">PVOutput.org api-add-status</a>
 */
public interface StatusServiceData {
	SimpleDate getDate();
	SimpleTime getTime();

	Number getEnergyGeneration();
	Number getPowerGeneration();
	Number getEnergyConsumption();
	Number getPowerConsumption();

	Float getTemperatureCelsius();
	Float getVoltage();

	Integer getCumulativeFlag();
	default boolean isDataCumulative(){
		Integer cumulativeFlag = getCumulativeFlag();
		return cumulativeFlag != null && cumulativeFlag == 1;
	}
	Integer getNetFlag();
	default boolean isDataNet(){
		Integer netFlag = getNetFlag();
		return netFlag != null && netFlag == 1;
	}

	/**
	 *
	 * @return An array with a length of 6 representing extended values. This will not be null, but the elements may be null
	 */
	Number[] getExtendedValues();
	String getTextMessage1();
}
