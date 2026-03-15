package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("CanBeFinal")
@JsonExplicit
@NullMarked
public abstract class DatabaseTimeZoneOptionBase extends TimeZoneOptionBase implements DatabaseOption, TimeZoneOption {

	@JsonProperty(value = "database", required = true)
	private @Nullable Path database;
	@JsonProperty(value = "source", required = true)
	private @Nullable String sourceId;

	@JsonProperty("include_undefined_sources")
	private @Nullable Boolean includeUndefinedSources = null;
	// The fact that we use DEFAULT_DEFAULT_INSTANCE_OPTIONS here is fine. The only database this matters on is the WMF database, which I have it configured correctly on
	@JsonProperty("default_fragment")
	private int defaultFragment = DefaultInstanceOptions.DEFAULT_DEFAULT_INSTANCE_OPTIONS.getDefaultFragmentId();
	@Override
	public Path getDatabaseFilePath() {
		return requireNonNull(database, "database must not be null");
	}
	@Override
	public String getSourceId(){
		return SourceIdValidator.validateSourceId(requireNonNull(sourceId, "sourceId must not be null"));
	}
	@Override
	public String getDefaultSourceId() {
		Boolean includeUndefinedSources = this.includeUndefinedSources;
		if (Boolean.TRUE.equals(includeUndefinedSources)) {
			return requireNonNull(sourceId, "sourceId must not be null");
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
