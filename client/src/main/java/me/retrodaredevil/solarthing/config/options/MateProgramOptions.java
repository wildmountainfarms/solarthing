package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.solar.outback.fx.charge.FXChargingSettings;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@JsonTypeName("mate")
public class MateProgramOptions extends PacketHandlingOptionBase implements IOBundleOption, ProgramOptions {

	@JsonProperty("allow_commands")
	private boolean allowCommands = false;
	@JsonProperty("ignore_check_sum")
	private boolean ignoreCheckSum = false;
	@JsonProperty("correct_check_sum")
	private boolean correctCheckSum = false;

	@JsonProperty(value = "io", required = true)
	private File io;

	@JsonProperty("fx_warning_ignore")
	private Map<Integer, Integer> fxWarningIgnoreMap;
	@JsonProperty("master_fx")
	private Integer masterFX = null;
	@JsonProperty("fx_charge_settings")
	private MateFXChargingSettings mateFXChargingSettings;

	@JsonProperty("commands")
	private List<Command> commands;

	public boolean isAllowCommands() {
		return allowCommands;
	}

	public boolean isIgnoreCheckSum() {
		return ignoreCheckSum;
	}
	public boolean isCorrectCheckSum() {
		return correctCheckSum;
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
	public @Nullable Integer getMasterFXAddress(){
		return masterFX;
	}
	public @Nullable FXChargingSettings getFXChargingSettings(){
		MateFXChargingSettings settings = this.mateFXChargingSettings;
		if(settings == null){
			return null;
		}
		return new FXChargingSettings(
				settings.rebulkSetpoint, settings.absorbSetpoint, Math.round(settings.absorbSetTimeLimit * 60 * 60 * 1000),
				settings.floatSetpoint, Math.round(settings.floatTimePeriod * 60 * 60 * 1000), settings.refloatSetpoint,
				settings.equalizeSetpoint, Math.round(settings.equalizeTimePeriod * 60 * 60 * 1000)
		);
	}
	public Map<String, File> getCommandFileMap() {
		Map<String, File> commandFileMap = new HashMap<>();
		for (Command command : commands) {
			commandFileMap.put(command.name, command.actionFile);
		}
		return commandFileMap;
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

	private static class Command {
		@JsonProperty(value = "name", required = true)
		private String name;
		@JsonProperty("display_name")
		private String displayName = null;
		@JsonProperty("description")
		private String description = "";
		@JsonProperty("action")
		private File actionFile;
	}
}
