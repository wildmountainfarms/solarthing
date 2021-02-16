package me.retrodaredevil.solarthing.couchdb;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.ektorp.ViewQuery;

@UtilityClass
public class SolarThingCouchDb {
	private SolarThingCouchDb() { throw new UnsupportedOperationException(); }

	public static ViewQuery createMillisView() {
		return new ViewQuery()
				.designDocId("_design/packets")
				.viewName("millis");
	}
}
