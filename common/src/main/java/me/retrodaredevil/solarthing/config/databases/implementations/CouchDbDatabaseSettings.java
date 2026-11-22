package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;
import okhttp3.HttpUrl;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@JsonTypeName("couchdb")
@JsonExplicit
@NullMarked
public final class CouchDbDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("couchdb");

	@JsonUnwrapped
	private final CouchProperties couchProperties;
	@JsonUnwrapped
	private final OkHttpProperties okHttpProperties;

	// Note: explicitly defining PROPERTIES mode here is required, as it otherwise is not correctly inferred since we have no arguments annotated with @JsonProperty
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public CouchDbDatabaseSettings(
			@JsonUnwrapped
			CouchProperties couchProperties,

			@JsonUnwrapped
			OkHttpProperties okHttpProperties
	) {
		this.couchProperties = requireNonNull(couchProperties, "couchProperties");
		this.okHttpProperties = requireNonNull(okHttpProperties, "okHttpProperties");
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

}
