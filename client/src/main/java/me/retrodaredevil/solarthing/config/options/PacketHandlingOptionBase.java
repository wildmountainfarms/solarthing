package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.request.DataRequester;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("FieldCanBeLocal")
abstract class PacketHandlingOptionBase extends TimeZoneOptionBase implements PacketHandlingOption, ActionsOption, CommandOption, AnalyticsOption {
//	private static final Logger LOGGER = LoggerFactory.getLogger(PacketHandlingOptionBase.class);

	@JsonProperty
	@JsonPropertyDescription("An array of strings that each represent a database configuration file relative to the program directory.")
	private List<Path> databases = null;
	@JsonProperty(value = "source", required = true)
	private String source = "default";
	@JsonProperty(value = "fragment", required = true)
	private int fragment;
	@JsonProperty
	private Integer unique = null;
	@JsonProperty("short")
	private boolean isShortId = true;

	@JsonProperty("request")
	private @Nullable List<DataRequester> dataRequesterList;

	@JsonProperty("commands")
	private List<CommandConfig> commandConfigs;

	@JsonProperty(AnalyticsOption.PROPERTY_NAME)
	private boolean isAnalyticsEnabled = AnalyticsOption.DEFAULT_IS_ANALYTICS_ENABLED;

	@JsonProperty("actions")
	private List<File> actionNodeFiles = new ArrayList<>();
	@JsonProperty("action_config")
	private ActionConfig actionConfig = ActionConfig.EMPTY;

	@Override
	public final @NotNull List<Path> getDatabaseConfigurationFilePaths() {
		List<Path> r = databases;
		if(r == null){
			return Collections.emptyList();
		}
		return r;
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

	@Override
	public final List<File> getActionNodeFiles() {
		return requireNonNull(actionNodeFiles, "You cannot use a null value for the actions property! Use an empty array or leave it undefined.");
	}

	@Override
	public final ActionConfig getActionConfig() {
		return requireNonNull(actionConfig);
	}
}
