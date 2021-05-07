package me.retrodaredevil.couchdbjava;

public final class CouchDbStatusCode {
	private CouchDbStatusCode() {
		throw new UnsupportedOperationException();
	}

	// https://docs.couchdb.org/en/latest/api/basics.html#http-status-codes

	public static final int CREATED = 201;
	public static final int ACCEPTED = 202;
	public static final int NOT_MODIFIED = 304;
	public static final int BAD_REQUEST = 400;
	public static final int UNAUTHORIZED = 401;
	public static final int NOT_FOUND = 404;
	public static final int UPDATE_CONFLICT = 409;
	public static final int PRECONDITION_FAILED = 412;
}
