package me.retrodaredevil.solarthing;

import static me.retrodaredevil.solarthing.SolarThingConstants.*;

public enum SolarThingDatabaseType {
	STATUS(STATUS_DATABASE, true), EVENT(EVENT_DATABASE, true),
	CLOSED(CLOSED_DATABASE, false), OPEN(OPEN_DATABASE, true),
	CACHE(CACHE_DATABASE, false),
//	ALTER(ALTER_DATABASE, false),
	;
	private final String name;
	private final boolean needsMillisView;

	SolarThingDatabaseType(String name, boolean needsMillisView) {
		this.name = name;
		this.needsMillisView = needsMillisView;
	}

	public boolean needsAnyViews() {
		return needsMillisView() || needsReadonlyView();
	}

	public boolean needsMillisView() {
		return needsMillisView;
	}

	public boolean needsReadonlyView() {
		// open database is only database where anyone can add documents to it even if they're unauthorized
		// cache doesn't need to be readonly because it is not public
		return this != OPEN && this != CACHE;
	}

	/**
	 * @return true if this database cannot have document added to by anyone except super admins
	 */
	public boolean isReadonlyByAll() {
		return this == CLOSED;
	}
	public boolean isPublic() {
		// all except CACHE need to be public because for all the other databases, we want to allow anyone to read them.
		// status, event -> anyone can read data
		// closed -> anyone can read metadata
		// open -> anyone can add documents
		// cache -> only people with access to this should be people with write access
		return this != CACHE;
	}

	public String getName() {
		return name;
	}
}
