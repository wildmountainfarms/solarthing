package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.option.DatabaseCreationOption;
import me.retrodaredevil.couchdbjava.request.BulkGetRequest;
import me.retrodaredevil.couchdbjava.request.BulkPostRequest;
import me.retrodaredevil.couchdbjava.request.ViewQuery;
import me.retrodaredevil.couchdbjava.request.ViewQueryParams;
import me.retrodaredevil.couchdbjava.response.*;
import me.retrodaredevil.couchdbjava.security.DatabaseSecurity;

import java.util.List;

public interface CouchDbDatabase {
	String getName();

	boolean exists() throws CouchDbException;
	void create(DatabaseCreationOption databaseCreationOption) throws CouchDbException;
	default void create() throws CouchDbException { create(DatabaseCreationOption.createDefault()); }
	boolean createIfNotExists(DatabaseCreationOption databaseCreationOption) throws CouchDbException;
	default boolean createIfNotExists() throws CouchDbException { return createIfNotExists(DatabaseCreationOption.createDefault()); }
	void deleteDatabase() throws CouchDbException;

	DatabaseInfo getDatabaseInfo() throws CouchDbException;

	DocumentResponse postNewDocument(JsonData jsonData) throws CouchDbException;

	/**
	 * Puts a document with the given id in the database. This will either create a new document,
	 * or may update an existing one. If updating an existing one, the json must have a `_rev` field
	 * with the value of the current revision of the desired document to update.
	 */
	DocumentResponse putDocument(String id, JsonData jsonData) throws CouchDbException;

	/**
	 * Similar to {@link #putDocument(String, JsonData)}, except this is only for updating existing
	 * documents
	 *
	 * Uses the "If-Match" header to update this document
	 */
	DocumentResponse updateDocument(String id, String revision, JsonData jsonData) throws CouchDbException;

	DocumentResponse deleteDocument(String id, String revision) throws CouchDbException;

	DocumentData getDocument(String id) throws CouchDbException;

	/**
	 *
	 * @param id The id of the document
	 * @param revision The revision of the document. If this is the latest revision, {@link me.retrodaredevil.couchdbjava.exception.CouchDbNotModifiedException} is thrown.
	 * @return DocumentData containing the data and revision of the retreived document
	 * @throws me.retrodaredevil.couchdbjava.exception.CouchDbNotModifiedException Thrown if the specified document is still at the given revision
	 * @throws CouchDbException May represent a connection error or that a document wasn't found, permission error, etc.
	 */
	DocumentData getDocumentIfUpdated(String id, String revision) throws CouchDbException;
	String getCurrentRevision(String id) throws CouchDbException;

	DocumentResponse copyToNewDocument(String id, String newDocumentId) throws CouchDbException;
	DocumentResponse copyFromRevisionToNewDocument(String id, String revision, String newDocumentId) throws CouchDbException;
	DocumentResponse copyToExistingDocument(String id, String targetDocumentId, String targetDocumentRevision) throws CouchDbException;
	DocumentResponse copyFromRevisionToExistingDocument(String id, String revision, String targetDocumentId, String targetDocumentRevision) throws CouchDbException;


	ViewResponse queryView(String designDoc, String viewName, ViewQueryParams viewQueryParams) throws CouchDbException;
	default ViewResponse queryView(ViewQuery viewQuery) throws CouchDbException {
		return queryView(viewQuery.getDesignDoc(), viewQuery.getViewName(), viewQuery.getParams());
	}

	DatabaseSecurity getSecurity() throws CouchDbException;
	void setSecurity(DatabaseSecurity databaseSecurity) throws CouchDbException;

	BulkGetResponse getDocumentsBulk(BulkGetRequest request) throws CouchDbException;
	List<BulkDocumentResponse> postDocumentsBulk(BulkPostRequest request) throws CouchDbException;

}
