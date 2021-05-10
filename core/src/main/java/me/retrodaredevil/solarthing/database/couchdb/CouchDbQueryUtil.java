package me.retrodaredevil.solarthing.database.couchdb;

import me.retrodaredevil.couchdbjava.ViewQueryParams;
import me.retrodaredevil.couchdbjava.ViewQueryParamsBuilder;
import me.retrodaredevil.solarthing.database.MillisQuery;

public final class CouchDbQueryUtil {
	private CouchDbQueryUtil() { throw new UnsupportedOperationException(); }

	public static ViewQueryParams createParamsFrom(MillisQuery millisQuery) {
		ViewQueryParamsBuilder builder = new ViewQueryParamsBuilder();
		if (millisQuery.getStartKey() != null) {
			builder.startKey(millisQuery.getStartKey());
		}
		if (millisQuery.getEndKey() != null) {
			builder.endKey(millisQuery.getEndKey());
		}
		if(!millisQuery.isInclusiveEnd()) {
			builder.inclusiveEnd(false);
		}
		builder.limit(millisQuery.getLimit());
		if (millisQuery.isDescending()) {
			builder.descending();
		}
		return builder.build();
	}

}
