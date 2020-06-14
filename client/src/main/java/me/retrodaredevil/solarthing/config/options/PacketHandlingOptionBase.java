package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("FieldCanBeLocal")
abstract class PacketHandlingOptionBase extends TimeZoneOptionBase implements PacketHandlingOption {
	@JsonProperty
	@JsonPropertyDescription("An array of strings that each represent a database configuration file relative to the program directory.")
	private List<File> databases = null;
	@JsonProperty(value = "source", required = true)
	private String source = "default";
	@JsonProperty(value = "fragment", required = true)
	private int fragment;
	@JsonProperty
	private Integer unique = null;

	@JsonProperty("extra_option_flags")
	private List<ExtraOptionFlag> extraOptionFlags;

	@Override
	public List<File> getDatabaseConfigurationFiles() {
		List<File> r = databases;
		if(r == null){
			return Collections.emptyList();
		}
		return r;
	}

	@Override
	public String getSourceId() {
		return requireNonNull(source);
	}

	@Override
	public int getFragmentId() {
		return fragment;
	}

	@Override
	public Integer getUniqueIdsInOneHour() {
		return unique;
	}

	@Override
	public List<ExtraOptionFlag> getExtraOptionFlags() {
		List<ExtraOptionFlag> r = extraOptionFlags;
		if(r == null){
			return Collections.emptyList();
		}
		return r;
	}
}
