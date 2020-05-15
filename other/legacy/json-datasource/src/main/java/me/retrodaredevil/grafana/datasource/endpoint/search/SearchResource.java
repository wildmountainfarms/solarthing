package me.retrodaredevil.grafana.datasource.endpoint.search;

import java.util.List;

public interface SearchResource {
	List<Object> getSuggestions(SearchRequest request);
}
