package me.retrodaredevil.solarthing.rest.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public class RestCommandConfig {
	private final Set<String> apiKeys;
	private final Map<String, Command> commandToActionFileMap;

	@JsonCreator
	public RestCommandConfig(
			@JsonProperty("keys") List<String> apiKeys,
			@JsonProperty("commands") Map<String, Command> commandToActionFileMap) {
		this.apiKeys = Set.copyOf(apiKeys);
		this.commandToActionFileMap = Map.copyOf(commandToActionFileMap);
	}

	public Set<String> getApiKeys() {
		return apiKeys;
	}

	public Map<String, Command> getCommandToActionFileMap() {
		return commandToActionFileMap;
	}

	@JsonExplicit
	public static class Command {
		private final File actionFile;
		/** The source ID that should be present in the InjectEnvironment or null to not include it.*/
		private final @Nullable String sourceId;

		@JsonCreator
		public Command(
				@JsonProperty(value = "action_file", required = true) File actionFile,
				@JsonProperty("source") @Nullable String sourceId) {
			requireNonNull(this.actionFile = actionFile);
			this.sourceId = sourceId;
		}

		public @NotNull File getActionFile() {
			return actionFile;
		}

		public @Nullable String getSourceId() {
			return sourceId;
		}
	}
}
