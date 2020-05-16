package me.retrodaredevil.solarthing.couchdb;

import org.ektorp.ViewQuery;

public class SolarThingCouchDb {
	private SolarThingCouchDb() { throw new UnsupportedOperationException(); }

	public static ViewQuery createMillisView() {
		return new ViewQuery()
				.designDocId("_design/packets")
				.viewName("millis");
	}
}
