package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;

import java.nio.file.Path;

@SuppressWarnings("CanBeFinal")
@JsonExplicit
public abstract class DatabaseTimeZoneOptionBase extends TimeZoneOptionBase implements DatabaseOption, TimeZoneOption {

	@JsonProperty(value = "database", required = true)
	private Path database;
	@JsonProperty(value = "source", required = true)
	private String sourceId = null;

	@JsonProperty("include_undefined_sources")
	private Boolean includeUndefinedSources = null;
	// The fact that we use DEFAULT_DEFAULT_INSTANCE_OPTIONS here is fine. The only database this matters on is the WMF database, which I have it configured correctly on
	@JsonProperty("default_fragment")
	private int defaultFragment = DefaultInstanceOptions.DEFAULT_DEFAULT_INSTANCE_OPTIONS.getDefaultFragmentId();
	@Override
	public Path getDatabaseFilePath() {
		return database;
	}
	@Override
	public String getSourceId(){
		return SourceIdValidator.validateSourceId(sourceId);
	}
	@Override
	public String getDefaultSourceId() {
		Boolean includeUndefinedSources = this.includeUndefinedSources;
		if (Boolean.TRUE.equals(includeUndefinedSources)) {
			return sourceId;
		}
		if (Boolean.FALSE.equals(includeUndefinedSources)) {
			return InstanceSourcePacket.UNUSED_SOURCE_ID;
		}
		return InstanceSourcePacket.DEFAULT_SOURCE_ID;
	}
	@Override
	public DefaultInstanceOptions getDefaultInstanceOptions() {
		return DefaultInstanceOptions.create(getDefaultSourceId(), defaultFragment);
	}
}
