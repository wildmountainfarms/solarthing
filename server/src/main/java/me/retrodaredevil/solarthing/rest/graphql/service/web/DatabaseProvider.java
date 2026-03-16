package me.retrodaredevil.solarthing.rest.graphql.service.web;

import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface DatabaseProvider {
	SolarThingDatabase getDatabase(@Nullable DatabaseAuthorization databaseAuthorization);

	/**
	 *
	 * @param username The username
	 * @param password The password
	 * @return The {@link DatabaseAuthorization}
	 * @throws RuntimeException Some form of this exception may be thrown if the authentication fails. We may update this exception in the future to a checked exception.
	 */
	DatabaseAuthorization authorize(String username, String password);
}
