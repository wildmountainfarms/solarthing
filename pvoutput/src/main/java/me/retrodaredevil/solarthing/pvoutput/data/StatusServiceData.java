package me.retrodaredevil.solarthing.pvoutput.data;

import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;

/**
 * @see <a href="https://pvoutput.org/help.html#api-addstatus">PVOutput.org api-add-status</a>
 */
public interface StatusServiceData {
	SimpleDate getDate();
	SimpleTime getTime();

	// One of these 4 methods must not return null
	Number getEnergyGeneration();
	/**
	 * @return If {@link #isDataCumulative()} net export power, otherwise power generation. May be null
	 */
	Number getPowerGeneration();
	Number getEnergyConsumption();
	Number getPowerConsumption();

	Float getTemperatureCelsius();
	Float getVoltage();

	Integer getCumulativeFlag();

	/**
	 * If cumulative, {@link #getEnergyGeneration()} and {@link #getEnergyConsumption()} will never reset.
	 * @return true if the data is cumulative, false otherwise
	 */
	default boolean isDataCumulative(){
		Integer cumulativeFlag = getCumulativeFlag();
		return cumulativeFlag != null && cumulativeFlag == 1;
	}
	Integer getNetFlag();

	/**
	 *
	 * @return true if the power values are net export/import rather than gross generation/consumption, false otherwise
	 * @see <a href="https://pvoutput.org/help.html#api-net-data">PVOutput.org net data</a>
	 */
	default boolean isDataNet(){
		Integer netFlag = getNetFlag();
		return netFlag != null && netFlag == 1;
	}

	/**
	 * Only available in donation mode
	 * @return An array with a length of 6 representing extended values. This will not be null, but the elements may be null
	 */
	Number[] getExtendedValues();

	/**
	 * Only available in donation mode
	 * @return A text message no greater than 30 characters.
	 */
	String getTextMessage1();
}
