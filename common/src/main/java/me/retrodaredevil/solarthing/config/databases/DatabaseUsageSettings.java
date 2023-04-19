package me.retrodaredevil.solarthing.config.databases;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.handling.FrequencySettings;

/**
 * Settings that define how a database configuration can be used in the program.
 * Settings may relate to how often a database should be accessed or uploaded to, or may disable or enable certain features of a database configuration.
 */
public class DatabaseUsageSettings {
	public static final DatabaseUsageSettings DEFAULT = new DatabaseUsageSettings(
			false,
			FrequencySettings.NORMAL_SETTINGS,
			FrequencySettings.NORMAL_SETTINGS
	);

	private final boolean inherit;
	private final FrequencySettings packetUploadFrequencySettings;
	private final FrequencySettings commandDownloadFrequencySettings;

	@JsonCreator
	public DatabaseUsageSettings(
			@JsonProperty("inherit") Boolean inherit,
			@JsonProperty("packet_upload") FrequencySettings packetUploadFrequencySettings,
			@JsonProperty("command_download") FrequencySettings commandDownloadFrequencySettings) {
		this.inherit = inherit == null || inherit; // defaults to true
		this.packetUploadFrequencySettings = packetUploadFrequencySettings;
		this.commandDownloadFrequencySettings = commandDownloadFrequencySettings;
	}

	public DatabaseUsageSettings inheritFrom(DatabaseUsageSettings baseDatabaseUsageSettings) {
		if (!inherit) {
			return this;
		}
		if (baseDatabaseUsageSettings.inherit) {
			baseDatabaseUsageSettings = baseDatabaseUsageSettings.inheritFrom(DEFAULT);
		}
		return new DatabaseUsageSettings(
				true,
				packetUploadFrequencySettings == null ? baseDatabaseUsageSettings.packetUploadFrequencySettings : packetUploadFrequencySettings,
				commandDownloadFrequencySettings == null ? baseDatabaseUsageSettings.commandDownloadFrequencySettings : commandDownloadFrequencySettings
		);
	}

	public @Nullable FrequencySettings getPacketUploadFrequencySettings() {
		return packetUploadFrequencySettings;
	}

	public @Nullable FrequencySettings getCommandDownloadFrequencySettings() {
		return commandDownloadFrequencySettings;
	}
}
