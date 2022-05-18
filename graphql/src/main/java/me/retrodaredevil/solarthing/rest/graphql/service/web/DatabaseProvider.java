package me.retrodaredevil.solarthing.rest.graphql.service.web;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;

public interface DatabaseProvider {
	@NotNull SolarThingDatabase getDatabase(@Nullable DatabaseAuthorization databaseAuthorization);
}
