package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;

public interface CouchDbInstance {
	CouchDbDatabase getDatabase(String path);
	CouchDbDatabase getReplicatorDatabase();
	CouchDbDatabase getUsersDatabase();

	SessionGetResponse getSessionInfo() throws CouchDbException;

	CouchDbGetResponse getInfo() throws CouchDbException;
}
