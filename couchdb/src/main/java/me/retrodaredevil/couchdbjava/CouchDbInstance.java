package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;

import java.util.List;

public interface CouchDbInstance {
	CouchDbDatabase getDatabase(String path);
	CouchDbDatabase getReplicatorDatabase();
	CouchDbDatabase getUsersDatabase();


	CouchDbGetResponse getInfo() throws CouchDbException;

	List<String> getAllDatabaseNames() throws CouchDbException;

	SessionGetResponse getSessionInfo() throws CouchDbException;

	List<DatabaseInfo> getDatabaseInfos(List<String> databaseNames) throws CouchDbException;
}
