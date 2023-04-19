package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.jackson.UnwrappedDeserializer;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;
import okhttp3.HttpUrl;

import java.net.URI;

import static java.util.Objects.requireNonNull;

@JsonTypeName("couchdb")
@JsonDeserialize(using = CouchDbDatabaseSettings.Deserializer.class)
@JsonExplicit
public final class CouchDbDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("couchdb");

	@JsonUnwrapped
	private final CouchProperties couchProperties;
	@JsonUnwrapped
	private final OkHttpProperties okHttpProperties;

	public CouchDbDatabaseSettings(CouchProperties couchProperties, OkHttpProperties okHttpProperties) {
		this.couchProperties = couchProperties;
		this.okHttpProperties = okHttpProperties;
	}

	@Override
	public String toString() {
		HttpUrl.Builder builder = couchProperties.getHttpUrl().newBuilder();
		CouchDbAuth auth = couchProperties.getAuth();
		if (auth.getUsername() != null) {
			builder.username(auth.getUsername());
		}
		return "CouchDB " + builder;
	}

	public CouchProperties getCouchProperties() {
		return couchProperties;
	}
	public OkHttpProperties getOkHttpProperties() {
		return okHttpProperties;
	}

	@Override
	public DatabaseType getDatabaseType() {
		return TYPE;
	}


	static class Deserializer extends UnwrappedDeserializer<CouchDbDatabaseSettings, Builder> {
		Deserializer() {
			super(Builder.class, Builder::build);
		}
	}

	static class Builder {

		@JsonUnwrapped
		@JsonProperty(required = true)
		private CouchProperties couchProperties;

		@JsonUnwrapped
		@JsonProperty(required = true)
		private OkHttpProperties okHttpProperties;

		public CouchDbDatabaseSettings build(){
			return new CouchDbDatabaseSettings(requireNonNull(couchProperties), requireNonNull(okHttpProperties));
		}
	}
}
