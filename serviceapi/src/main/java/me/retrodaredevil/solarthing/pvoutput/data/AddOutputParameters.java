package me.retrodaredevil.solarthing.pvoutput.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.pvoutput.PVOutputString;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import me.retrodaredevil.solarthing.pvoutput.WeatherCondition;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonExplicit
public interface AddOutputParameters {
	@JsonProperty("d")
	SimpleDate getOutputDate();

	/**
	 * Normally this should not be null. This is required if the output does not already exist
	 * @return The number of generated watt hours, or null
	 */
	@JsonProperty("g")
	Number getGenerated();
	/** @return The number of exported watt hours*/
	@JsonProperty("e")
	Number getExported();
	/** @return The peak power in watts*/
	@JsonProperty("pp")
	Number getPeakPower();
	@JsonProperty("pt")
	SimpleTime getPeakTime();

	@JsonProperty("cd")
	String getConditionValue();
	@SuppressWarnings("unused")
	default WeatherCondition getCondition(){
		String value = getConditionValue();
		if(value == null){
			return null;
		}
		return WeatherCondition.getConditionFromStringOrNull(value);
	}

	@JsonProperty("tm")
	Float getMinimumTemperatureCelsius();
	@JsonProperty("tx")
	Float getMaximumTemperatureCelsius();

	@JsonProperty("cm")
	String getComments();

	@JsonProperty("ip")
	Number getImportPeak();
	@JsonProperty("io")
	Number getImportOffPeak();
	@JsonProperty("is")
	Number getImportShoulder();
	@JsonProperty("ih")
	Number getImportHighShoulder();
	@JsonProperty("c")
	Number getConsumption();

	@JsonProperty("ep")
	Number getExportPeak();
	@JsonProperty("eo")
	Number getExportOffPeak();
	@JsonProperty("es")
	Number getExportShoulder();
	@JsonProperty("eh")
	Number getExportHighShoulder();

	@Deprecated
	default String[] toBatchCsvArray() {
		return new String[] {
				getOutputDate().toPVOutputString(),
				toStringOrNull(getGenerated()),
				toStringOrNull(getExported()),
				toStringOrNull(getConsumption()),
				toStringOrNull(getPeakPower()),
				toStringOrNull(getPeakTime()),
				getConditionValue(),
				toStringOrNull(getMinimumTemperatureCelsius()),
				toStringOrNull(getMaximumTemperatureCelsius()),
				getComments(),
				toStringOrNull(getImportPeak()),
				toStringOrNull(getImportOffPeak()),
				toStringOrNull(getImportShoulder())
		};
	}
	default String[] toCsvArray() {
		return new String[] {
				getOutputDate().toPVOutputString(),
				toStringOrNull(getGenerated()),
				toStringOrNull(getExported()),
				toStringOrNull(getPeakPower()),
				toStringOrNull(getPeakTime()),
				getConditionValue(),
				toStringOrNull(getMinimumTemperatureCelsius()),
				toStringOrNull(getMaximumTemperatureCelsius()),
				getComments(),
				toStringOrNull(getImportPeak()),
				toStringOrNull(getImportOffPeak()),
				toStringOrNull(getImportShoulder()),
				toStringOrNull(getImportHighShoulder()),
				toStringOrNull(getConsumption()),
				toStringOrNull(getExportPeak()),
				toStringOrNull(getExportOffPeak()),
				toStringOrNull(getExportShoulder()),
				toStringOrNull(getExportHighShoulder()),
		};
	}
	static String toStringOrNull(Object object) {
		if(object == null) {
			return null;
		}
		if (object instanceof PVOutputString) {
			return ((PVOutputString) object).toPVOutputString();
		}
		return object.toString();
	}
}
