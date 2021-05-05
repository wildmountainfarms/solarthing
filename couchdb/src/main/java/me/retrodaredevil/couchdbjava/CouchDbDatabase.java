package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.option.DatabaseCreationOption;
import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.DocumentData;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.ViewResponse;

public interface CouchDbDatabase {
	String getName();

	boolean exists() throws CouchDbException;
	void create(DatabaseCreationOption databaseCreationOption) throws CouchDbException;
	default void create() throws CouchDbException { create(DatabaseCreationOption.createDefault()); }
	boolean createIfNotExists(DatabaseCreationOption databaseCreationOption) throws CouchDbException;
	default boolean createIfNotExists() throws CouchDbException { return createIfNotExists(DatabaseCreationOption.createDefault()); }
	void deleteDatabase() throws CouchDbException;

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

	DocumentData getDocument(String id) throws CouchDbException;
	String getCurrentRevision(String id) throws CouchDbException;

	DocumentResponse copyToNewDocument(String id, String newDocumentId) throws CouchDbException;
	DocumentResponse copyFromRevisionToNewDocument(String id, String revision, String newDocumentId) throws CouchDbException;
	DocumentResponse copyToExistingDocument(String id, String targetDocumentId, String targetDocumentRevision) throws CouchDbException;
	DocumentResponse copyFromRevisionToExistingDocument(String id, String revision, String targetDocumentId, String targetDocumentRevision) throws CouchDbException;


	ViewResponse queryView(String designDoc, String viewName, ViewQuery viewQuery) throws CouchDbException;

}
