package me.retrodaredevil.solarthing.pvoutput.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.pvoutput.PVOutputString;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import me.retrodaredevil.solarthing.pvoutput.WeatherCondition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonExplicit
@NullMarked
public interface AddOutputParameters {
	@JsonProperty("d")
	SimpleDate getOutputDate();

	/**
	 * Normally this should not be null. This is required if the output does not already exist
	 * @return The number of generated watt hours, or null
	 */
	@JsonProperty("g")
	@Nullable Number getGenerated();
	/** @return The number of exported watt hours*/
	@JsonProperty("e")
	@Nullable Number getExported();
	/** @return The peak power in watts*/
	@JsonProperty("pp")
	@Nullable Number getPeakPower();
	@JsonProperty("pt")
	@Nullable SimpleTime getPeakTime();

	@JsonProperty("cd")
	@Nullable String getConditionValue();
	@SuppressWarnings("unused")
	default @Nullable WeatherCondition getCondition(){
		String value = getConditionValue();
		if(value == null){
			return null;
		}
		return WeatherCondition.getConditionFromStringOrNull(value);
	}

	@JsonProperty("tm")
	@Nullable Float getMinimumTemperatureCelsius();
	@JsonProperty("tx")
	@Nullable Float getMaximumTemperatureCelsius();

	@JsonProperty("cm")
	@Nullable String getComments();

	@JsonProperty("ip")
	@Nullable Number getImportPeak();
	@JsonProperty("io")
	@Nullable Number getImportOffPeak();
	@JsonProperty("is")
	@Nullable Number getImportShoulder();
	@JsonProperty("ih")
	@Nullable Number getImportHighShoulder();
	@JsonProperty("c")
	@Nullable Number getConsumption();

	@JsonProperty("ep")
	@Nullable Number getExportPeak();
	@JsonProperty("eo")
	@Nullable Number getExportOffPeak();
	@JsonProperty("es")
	@Nullable Number getExportShoulder();
	@JsonProperty("eh")
	@Nullable Number getExportHighShoulder();

	default @Nullable String[] toCsvArray() {
		return new @Nullable String[] {
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
	static @Nullable String toStringOrNull(@Nullable Object object) {
		if(object == null) {
			return null;
		}
		if (object instanceof PVOutputString) {
			return ((PVOutputString) object).toPVOutputString();
		}
		return object.toString();
	}
}
