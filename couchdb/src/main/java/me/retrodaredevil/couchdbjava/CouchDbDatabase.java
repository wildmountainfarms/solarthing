package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;

public interface CouchDbDatabase {
	boolean exists() throws CouchDbException;
	boolean createIfNotExists() throws CouchDbException;
	void delete() throws CouchDbException;
	String getName();

	DatabaseInfo getDatabaseInfo() throws CouchDbException;

	String createWithoutIdRaw(String json) throws CouchDbException;

	String putRaw(String id, String json) throws CouchDbException;

	/**
	 * @param id
	 * @param json
	 * @return
	 */
	default String createRaw(String id, String json) throws CouchDbException{
		return putRaw(id, json);
	}

	default String updateRaw(String id, String json) throws CouchDbException {
		return putRaw(id, json);
	}

	/**
	 * Uses the "If-Match" header to update this document
	 * @param id
	 * @param revision
	 * @param json
	 * @return
	 */
	String updateWithRevision(String id, String revision, String json) throws CouchDbException;

	void delete(String id, String revision) throws CouchDbException;

	String findRaw(String id) throws CouchDbException;
	String getCurrentRevision(String id) throws CouchDbException;

	String copy(String sourceId, String targetId, String targetRevision) throws CouchDbException;
	default String copy(String sourceId, String targetId) throws CouchDbException {
		return copy(sourceId, targetId, null);
	}
}
