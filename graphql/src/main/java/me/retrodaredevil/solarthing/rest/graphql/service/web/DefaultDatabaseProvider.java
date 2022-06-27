package me.retrodaredevil.solarthing.rest.graphql.service.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.BasicAuthHandler;
import me.retrodaredevil.couchdbjava.okhttp.auth.CookieAuthHandler;
import me.retrodaredevil.okhttp3.OkHttpUtil;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParsingErrorHandler;
import me.retrodaredevil.solarthing.rest.exceptions.DatabaseException;
import okhttp3.Cookie;

import static java.util.Objects.requireNonNull;

public class DefaultDatabaseProvider implements DatabaseProvider {

	private final CouchDbDatabaseSettings couchDbDatabaseSettings;
	private final ObjectMapper objectMapper;

	private final OkHttpCouchDbInstance noAuthInstance;
	private final CouchDbInstance defaultInstance;

	public DefaultDatabaseProvider(CouchDbDatabaseSettings couchDbDatabaseSettings, ObjectMapper objectMapper) {
		this.couchDbDatabaseSettings = couchDbDatabaseSettings;
		this.objectMapper = objectMapper;

		noAuthInstance = new OkHttpCouchDbInstance(OkHttpUtil.createBuilder(couchDbDatabaseSettings.getOkHttpProperties()).build(), couchDbDatabaseSettings.getCouchProperties().getHttpUrl(), new BasicAuthHandler(CouchDbAuth.createNoAuth()));
		defaultInstance = CouchDbUtil.createInstance(couchDbDatabaseSettings.getCouchProperties(), couchDbDatabaseSettings.getOkHttpProperties());
	}

	@Override
	public @NotNull SolarThingDatabase getDatabase(@Nullable DatabaseAuthorization databaseAuthorization) {
		final CouchDbInstance instance;
		if (databaseAuthorization == null) {
			instance = defaultInstance;
		} else {
			// note that in this case, we ignore couchDbDatabaseSettings.getCouchProperties().useBasicAuth()
			instance = new OkHttpCouchDbInstance(
					OkHttpUtil.createBuilder(couchDbDatabaseSettings.getOkHttpProperties()).build(),
					couchDbDatabaseSettings.getCouchProperties().getHttpUrl(),
					new ExistingCookieAuthHandler(databaseAuthorization.getCookie())
			);
		}
		// Notice that objectMapper is likely not lenient, but that's OK. We expect the user to keep this program up to date
		return new CouchDbSolarThingDatabase(instance, PacketParsingErrorHandler.DO_NOTHING, objectMapper);
	}

	@Override
	public @NotNull DatabaseAuthorization authorize(String username, String password) {
		CookieAuthHandler cookieAuthHandler = new CookieAuthHandler(username, password);
		try {
			cookieAuthHandler.authSession(noAuthInstance);
		} catch (CouchDbException e) {
			throw new DatabaseException(e);
		}
		Cookie cookie = requireNonNull(cookieAuthHandler.getAuthCookie());
		return DatabaseAuthorization.create(cookie);
	}
}
