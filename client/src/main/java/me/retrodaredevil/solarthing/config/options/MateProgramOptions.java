package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@JsonTypeName("mate")
@JsonIgnoreProperties("allow_commands")
public class MateProgramOptions extends PacketHandlingOptionBase implements IOBundleOption, AnalyticsOption, ProgramOptions, CommandOption {

	@JsonProperty("ignore_check_sum")
	private boolean ignoreCheckSum = false;
	@JsonProperty("correct_check_sum")
	private boolean correctCheckSum = false;

	@JsonProperty(value = "io", required = true)
	private File io;

	@JsonProperty("fx_warning_ignore")
	private Map<Integer, Integer> fxWarningIgnoreMap;

	@JsonProperty("commands")
	private List<Command> commands;

	@JsonProperty(AnalyticsOption.PROPERTY_NAME)
	private boolean isAnalyticsEnabled = AnalyticsOption.DEFAULT_IS_ANALYTICS_ENABLED;


	@Override
	public @Nullable List<Command> getDeclaredCommandsNullable() {
		return commands;
	}

	public boolean isIgnoreCheckSum() {
		return ignoreCheckSum;
	}
	public boolean isCorrectCheckSum() {
		return correctCheckSum;
	}

	@Override
	public boolean isAnalyticsOptionEnabled() {
		return isAnalyticsEnabled;
	}

	@Override
	public File getIOBundleFile() {
		return requireNonNull(io, "io is required!");
	}

	public static IgnoreCheckSum getIgnoreCheckSum(MateProgramOptions options) {
		if(options.isCorrectCheckSum()){
			return IgnoreCheckSum.IGNORE_AND_USE_CALCULATED;
		} else if(options.isIgnoreCheckSum()){
			return IgnoreCheckSum.IGNORE;
		}
		return IgnoreCheckSum.DISABLED;
	}

	@Override
	public ProgramType getProgramType() {
		return ProgramType.MATE;
	}
	public Map<Integer, Integer> getFXWarningIgnoreMap() {
		Map<Integer, Integer> r = fxWarningIgnoreMap;
		if(r == null){
			return Collections.emptyMap();
		}
		return r;
	}

	private static class MateFXChargingSettings {
		@JsonProperty("rebulk_voltage")
		private Float rebulkSetpoint;

		@JsonProperty(value = "absorb_voltage", required = true)
		private float absorbSetpoint;
		@JsonProperty(value = "absorb_time_hours", required = true)
		private double absorbSetTimeLimit;

		@JsonProperty(value = "float_voltage", required = true)
		private float floatSetpoint;
		@JsonProperty(value = "float_time_hours", required = true)
		private double floatTimePeriod;
		@JsonProperty(value = "refloat_voltage", required = true)
		private float refloatSetpoint;

		@JsonProperty(value = "equalize_voltage", required = true)
		private float equalizeSetpoint;
		@JsonProperty(value = "equalize_time_hours", required = true)
		private double equalizeTimePeriod;
	}

}
