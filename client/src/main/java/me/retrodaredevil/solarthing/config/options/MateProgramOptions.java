package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@JsonTypeName("mate")
public final class MateProgramOptions extends PacketHandlingOptionBase implements IOBundleOption, ProgramOptions {

	private final boolean ignoreCheckSum;
	private final boolean correctCheckSum;

	private final Path io;
	private final Map<Integer, Integer> fxWarningIgnoreMap;

	@JsonCreator
	public MateProgramOptions(
			@JsonProperty("ignore_check_sum") Boolean ignoreCheckSum,
			@JsonProperty("correct_check_sum") Boolean correctCheckSum,
			@JsonProperty(value = "io", required = true) Path io,
			@JsonProperty("fx_warning_ignore") Map<Integer, Integer> fxWarningIgnoreMap) {
		this.ignoreCheckSum = Boolean.TRUE.equals(ignoreCheckSum);
		this.correctCheckSum = Boolean.TRUE.equals(correctCheckSum);
		this.io = requireNonNull(io);
		this.fxWarningIgnoreMap = fxWarningIgnoreMap == null ? Collections.emptyMap() : fxWarningIgnoreMap;
	}


	@Override
	public Path getIOBundleFilePath() {
		return io;
	}

	public IgnoreCheckSum getIgnoreCheckSum() {
		if(correctCheckSum){
			return IgnoreCheckSum.IGNORE_AND_USE_CALCULATED;
		} else if(ignoreCheckSum){
			return IgnoreCheckSum.IGNORE;
		}
		return IgnoreCheckSum.DISABLED;
	}

	@Override
	public ProgramType getProgramType() {
		return ProgramType.MATE;
	}
	public Map<Integer, Integer> getFXWarningIgnoreMap() {
		return fxWarningIgnoreMap;
	}

}
