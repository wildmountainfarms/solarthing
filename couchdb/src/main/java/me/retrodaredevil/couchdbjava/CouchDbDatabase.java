package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;

public interface CouchDbDatabase {
	boolean exists() throws CouchDbException;
	boolean createIfNotExists() throws CouchDbException;
	void create() throws CouchDbException;
	void delete() throws CouchDbException;
	String getName();

	DatabaseInfo getDatabaseInfo() throws CouchDbException;

	DocumentResponse postNewDocument(String json) throws CouchDbException;

	/**
	 * Puts a document with the given id in the database. This will either create a new document,
	 * or may update an existing one. If updating an existing one, the json must have a `_rev` field
	 * with the value of the current revision of the desired document to update.
	 */
	DocumentResponse putDocument(String id, String json) throws CouchDbException;

	/**
	 * Similar to {@link #putDocument(String, String)}, except this is only for updating existing
	 * documents
	 *
	 * Uses the "If-Match" header to update this document
	 */
	DocumentResponse updateDocument(String id, String revision, String json) throws CouchDbException;

	DocumentResponse deleteDocument(String id, String revision) throws CouchDbException;

	String findRaw(String id) throws CouchDbException;
	String getCurrentRevision(String id) throws CouchDbException;

	String copy(String sourceId, String targetId, String targetRevision) throws CouchDbException;
	default String copy(String sourceId, String targetId) throws CouchDbException {
		return copy(sourceId, targetId, null);
	}
}
