package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.database.SolarThingDatabase;

public class SolarThingDatabaseEnvironment {
	private final SolarThingDatabase solarThingDatabase;

	public SolarThingDatabaseEnvironment(SolarThingDatabase solarThingDatabase) {
		this.solarThingDatabase = solarThingDatabase;
	}

	public SolarThingDatabase getSolarThingDatabase() {
		return solarThingDatabase;
	}
}
