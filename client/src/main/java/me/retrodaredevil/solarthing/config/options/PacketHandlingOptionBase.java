package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.databases.DatabaseConfig;
import me.retrodaredevil.solarthing.config.request.DataRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
abstract class PacketHandlingOptionBase extends TimeZoneOptionBase implements PacketHandlingOption, ActionsOption, CommandOption, AnalyticsOption {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketHandlingOptionBase.class);

	@JsonProperty("databases")
	@JsonPropertyDescription("An array of strings that each represent a database configuration file relative to the program directory.")
	private List<Path> databases = null;
	@JsonProperty("database_config")
	private DatabaseConfigSettings databaseConfigSettings = null;
	@JsonProperty(value = "source", required = true)
	private String source = "default";
	@JsonProperty(value = "fragment", required = true)
	private int fragment;
	@JsonProperty("unique")
	private Integer unique = null;
	@JsonProperty("short")
	private boolean isShortId = true;

	@JsonProperty("request")
	private @Nullable List<DataRequester> dataRequesterList;

	@JsonProperty("commands")
	private List<CommandConfig> commandConfigs;

	@JsonProperty(AnalyticsOption.PROPERTY_NAME)
	private boolean isAnalyticsEnabled = AnalyticsOption.DEFAULT_IS_ANALYTICS_ENABLED;

	@JsonProperty("action_config")
	private ActionConfig actionConfig = ActionConfig.EMPTY;

	@Deprecated
	@Override
	public final @NotNull List<Path> getDatabaseConfigurationFilePaths() {
		List<Path> r = databases;
		if(r == null){
			return Collections.emptyList();
		}
		return r;
	}

	@Override
	public @NotNull DatabaseConfigSettings getDatabaseConfigSettings() {
		if (databaseConfigSettings == null) {
			if(databases == null){
				return DatabaseConfigSettings.EMPTY;
			}
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "(Deprecated) Do not use the databases property! Use database_config instead!");
			List<DatabaseConfig> databaseConfigs = databases.stream().map(DatabaseConfig::fromExternal).collect(Collectors.toList());
			return new DatabaseConfigSettings(databaseConfigs);
		}
		return databaseConfigSettings;
	}

	@Override
	public final @NotNull String getSourceId() {
		return SourceIdValidator.validateSourceId(source);
	}

	@Override
	public final int getFragmentId() {
		return fragment;
	}

	@Override
	public final Integer getUniqueIdsInOneHour() {
		return unique;
	}

	@Override
	public final boolean isDocumentIdShort() {
		return isShortId;
	}

	@Override
	public final @NotNull List<DataRequester> getDataRequesterList() {
		List<DataRequester> r = dataRequesterList;
		if (r == null) {
			return Collections.emptyList();
		}
		return r;
	}

	@Override
	public final @Nullable List<CommandConfig> getDeclaredCommandsNullable() {
		return commandConfigs;
	}
	@Override
	public final boolean isAnalyticsOptionEnabled() {
		return isAnalyticsEnabled;
	}

	@JsonSetter("actions")
	private void setActionNodeFiles(List<String> actionNodeFiles) {
		LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "(Deprecated) Please use action_config configuration instead of actions!");
		// TODO use a different exception here
		throw new RuntimeException("Migration required! You must now use action_config instead of actions to configure actions!");
	}

	@Override
	public final ActionConfig getActionConfig() {
		return requireNonNull(actionConfig);
	}
}
