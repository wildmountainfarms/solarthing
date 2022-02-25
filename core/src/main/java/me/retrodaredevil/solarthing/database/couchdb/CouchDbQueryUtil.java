package me.retrodaredevil.solarthing.database.couchdb;

import me.retrodaredevil.couchdbjava.request.ViewQueryParams;
import me.retrodaredevil.couchdbjava.request.ViewQueryParamsBuilder;
import me.retrodaredevil.solarthing.database.MillisQuery;

public final class CouchDbQueryUtil {
	private CouchDbQueryUtil() { throw new UnsupportedOperationException(); }

	public static ViewQueryParams createMillisNullParams(MillisQuery millisQuery) {
		ViewQueryParamsBuilder builder = new ViewQueryParamsBuilder()
				.includeDocs(true);
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
		// Consider testing these commented out lines. https://docs.couchdb.org/en/stable/maintenance/performance.html#views-generation
//		builder.stable(false)
//				.update(ViewQueryParamsBuilder.Update.FALSE);
		return builder.build();
	}

}
