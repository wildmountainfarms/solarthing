package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import okhttp3.HttpUrl;

@JsonDeserialize(as = ImmutableCouchProperties.class)
@JsonExplicit
public interface CouchProperties {

	@NotNull HttpUrl getHttpUrl();

	@JsonProperty("url")
	@NotNull String getUrl();

	@JsonProperty("protocol")
	@NotNull String getProtocol();

	@JsonProperty("host")
	@NotNull String getHost();

	@JsonProperty("path")
	@Nullable String getPath();

	@JsonProperty("port")
	int getPort();

	@JsonProperty("username")
	@Nullable String getUsername();

	@JsonProperty("password")
	@Nullable String getPassword();

	@JsonProperty("basic_auth")
	boolean useBasicAuth();
}
