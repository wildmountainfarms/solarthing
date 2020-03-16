package me.retrodaredevil.solarthing.pvoutput.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;

/**
 * @see <a href="https://pvoutput.org/help.html#api-addstatus">PVOutput.org api-add-status</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonExplicit
public interface AddStatusParameters {
	@JsonProperty("d")
	SimpleDate getDate();
	@JsonProperty("t")
	SimpleTime getTime();

	// One of these 4 methods must not return null
	@JsonProperty("v1")
	Number getEnergyGeneration();
	/**
	 * @return If {@link #isDataCumulative()} net export power, otherwise power generation. May be null
	 */
	@JsonProperty("v2")
	Number getPowerGeneration();
	@JsonProperty("v3")
	Number getEnergyConsumption();
	@JsonProperty("v4")
	Number getPowerConsumption();

	@JsonProperty("v5")
	Float getTemperatureCelsius();
	@JsonProperty("v6")
	Float getVoltage();

	@JsonProperty("c1")
	Integer getCumulativeFlag();

	/**
	 * If cumulative, {@link #getEnergyGeneration()} and {@link #getEnergyConsumption()} will never reset.
	 * @return true if the data is cumulative, false otherwise
	 */
	default boolean isDataCumulative(){
		Integer cumulativeFlag = getCumulativeFlag();
		return cumulativeFlag != null && cumulativeFlag == 1;
	}
	@JsonProperty("n")
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

	@JsonProperty("v7")
	Number getExtendedValue1();
	@JsonProperty("v8")
	Number getExtendedValue2();
	@JsonProperty("v9")
	Number getExtendedValue3();
	@JsonProperty("v10")
	Number getExtendedValue4();
	@JsonProperty("v11")
	Number getExtendedValue5();
	@JsonProperty("v12")
	Number getExtendedValue6();

	/**
	 * Only available in donation mode
	 * @return A text message no greater than 30 characters.
	 */
	@JsonProperty("m1")
	String getTextMessage1();
}
