package me.retrodaredevil.solarthing.rest.graphql.service.web;

import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;

public class SolarThingAdminService {

	private final DatabaseProvider databaseProvider;

	public SolarThingAdminService(DatabaseProvider databaseProvider) {
		this.databaseProvider = databaseProvider;
	}

	@GraphQLQuery
	public DatabaseAuthorization databaseAuthorize(@NotNull String username, @NotNull String password) {
		return databaseProvider.authorize(username, password);
	}
}
