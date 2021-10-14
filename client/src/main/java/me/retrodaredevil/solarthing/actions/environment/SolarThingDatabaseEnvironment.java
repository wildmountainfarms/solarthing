package me.retrodaredevil.action.node.environment;

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
