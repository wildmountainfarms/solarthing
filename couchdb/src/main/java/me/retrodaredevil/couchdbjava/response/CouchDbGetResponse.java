package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class CouchDbGetResponse {
	private final String message;
	private final String version;
	private final @Nullable String gitSha;
	private final String uuid;
	private final List<String> features;
	private final Vendor vendor;

	@JsonCreator
	public CouchDbGetResponse(
			@JsonProperty(value = "couchdb", required = true) String message,
			@JsonProperty(value = "version", required = true) String version,
			@JsonProperty("git_sha") @Nullable String gitSha,
			@JsonProperty(value = "uuid", required = true) String uuid,
			@JsonProperty(value = "features") List<String> features,
			@JsonProperty(value = "vendor", required = true) Vendor vendor) {
		requireNonNull(this.message = message);
		requireNonNull(this.version = version);
		this.gitSha = gitSha;
		requireNonNull(this.uuid = uuid);
		this.features = features == null ? Collections.emptyList() : features;
		requireNonNull(this.vendor = vendor);
	}

	@JsonProperty("couchdb")
	public String getMessage() {
		return message;
	}

	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	@JsonProperty("git_sha")
	public String getGitSha() {
		return gitSha;
	}

	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@JsonProperty("features")
	public List<String> getFeatures() {
		return features;
	}

	@JsonProperty("vendor")
	public Vendor getVendor() {
		return vendor;
	}


	public static class Vendor {
		private final String name;
		private final @Nullable String version;

		@JsonCreator
		public Vendor(
				@JsonProperty(value = "name", required = true) String name,
				@JsonProperty("version") @Nullable String version) {
			requireNonNull(this.name = name);
			this.version = version;
		}

		@JsonProperty("name")
		public String getName() {
			return name;
		}

		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonProperty("version")
		public @Nullable String getVersion() {
			return version;
		}
	}
}
