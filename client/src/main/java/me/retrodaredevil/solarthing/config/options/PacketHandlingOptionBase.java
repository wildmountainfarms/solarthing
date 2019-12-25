package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.io.File;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
class PacketHandlingOptionBase implements PacketHandlingOption {
	@JsonProperty
	@JsonPropertyDescription("An array of strings that each represent a database configuration file relative to the program directory.")
	private List<File> databases = null;
	@JsonProperty
	private String source = "default";
	@JsonProperty
	private Integer fragment = null;
	@JsonProperty
	private Integer unique = null;

	@Override
	public List<File> getDatabaseConfigurationFiles() {
		return databases;
	}

	@Override
	public String getSourceId() {
		return source;
	}

	@Override
	public Integer getFragmentId() {
		return fragment;
	}

	@Override
	public Integer getUniqueIdsInOneHour() {
		return unique;
	}
}
