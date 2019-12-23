package me.retrodaredevil.grafana.datasource.endpoint.query;

import java.util.List;

public interface QueryResource {
	List<QueryResult> queryMetrics(QueryRequest request);
}
