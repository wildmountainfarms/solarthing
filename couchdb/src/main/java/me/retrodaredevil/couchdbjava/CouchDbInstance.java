package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;

public interface CouchDbInstance {
	CouchDbDatabase getDatabase(String path);
	CouchDbDatabase getReplicatorDatabase();
	CouchDbDatabase getUsersDatabase();

	String authSession(String username, String password) throws CouchDbException;
}
