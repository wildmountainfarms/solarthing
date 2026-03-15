package me.retrodaredevil.solarthing.pvoutput.data;

import org.jspecify.annotations.NullMarked;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import org.jspecify.annotations.Nullable;

/**
 * @see <a href="https://pvoutput.org/help.html#api-addstatus">PVOutput.org api-add-status</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonExplicit

@NullMarked
public interface AddStatusParameters {
	@JsonProperty("d")
	SimpleDate getDate();
	@JsonProperty("t")
	SimpleTime getTime();

	// One of these 4 methods must not return null
	@JsonProperty("v1")
	@Nullable Number getEnergyGeneration();
	/**
	 * @return If {@link #isDataCumulative()} net export power, otherwise power generation. May be null
	 */
	@JsonProperty("v2")
	@Nullable Number getPowerGeneration();
	@JsonProperty("v3")
	@Nullable Number getEnergyConsumption();
	@JsonProperty("v4")
	@Nullable Number getPowerConsumption();

	@JsonProperty("v5")
	@Nullable Float getTemperatureCelsius();
	@JsonProperty("v6")
	@Nullable Float getVoltage();

	@JsonProperty("c1")
	@Nullable Integer getCumulativeFlag();

	/**
	 * If cumulative, {@link #getEnergyGeneration()} and {@link #getEnergyConsumption()} will never reset.
	 * @return true if the data is cumulative, false otherwise
	 */
	default boolean isDataCumulative(){
		Integer cumulativeFlag = getCumulativeFlag();
		return cumulativeFlag != null && cumulativeFlag == 1;
	}
	@JsonProperty("n")
	@Nullable Integer getNetFlag();

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
	@Nullable Number getExtendedValue1();
	@JsonProperty("v8")
	@Nullable Number getExtendedValue2();
	@JsonProperty("v9")
	@Nullable Number getExtendedValue3();
	@JsonProperty("v10")
	@Nullable Number getExtendedValue4();
	@JsonProperty("v11")
	@Nullable Number getExtendedValue5();
	@JsonProperty("v12")
	@Nullable Number getExtendedValue6();

	/**
	 * Only available in donation mode
	 * @return A text message no greater than 30 characters.
	 */
	@JsonProperty("m1")
	@Nullable String getTextMessage1();
}
