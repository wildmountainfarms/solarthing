package me.retrodaredevil.solarthing.rest.graphql.service.web;

import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface DatabaseProvider {
	@NonNull SolarThingDatabase getDatabase(@Nullable DatabaseAuthorization databaseAuthorization);

	/**
	 *
	 * @param username The username
	 * @param password The password
	 * @return The {@link DatabaseAuthorization}
	 * @throws RuntimeException Some form of this exception may be thrown if the authentication fails. We may update this exception in the future to a checked exception.
	 */
	@NonNull DatabaseAuthorization authorize(String username, String password);
}
