package me.retrodaredevil.couchdb.design;

import java.util.HashMap;
import java.util.Map;

public class MutablePacketsDesign implements Design {
	private final Map<String, View> views = new HashMap<>();
	private final Map<String, String> filters = new HashMap<>();
	private String validateDocUpdate = null;

	public MutablePacketsDesign addMillisNullView() {
		views.put("millisNull", new SimpleView(DesignResource.VIEW_JAVASCRIPT_MILLIS_NULL.getAsString()));
		return this;
	}
	public MutablePacketsDesign addSimpleAllDocsView() {
		views.put("simpleAllDocs", new SimpleView(DesignResource.VIEW_JAVASCRIPT_SIMPLE_ALL_DOCS.getAsString()));
		return this;
	}
	public MutablePacketsDesign setReadonlyAuth() {
		this.validateDocUpdate = DesignResource.VALIDATE_JAVASCRIPT_READONLY_AUTH.getAsString();
		return this;
	}
	public MutablePacketsDesign addLast24HoursFilter() {
		filters.put("last24Hours", DesignResource.FILTER_JAVASCRIPT_RECENT_PACKETS.getAsString());
		return this;
	}

	@Override
	public Map<String, View> getViews() {
		return views;
	}

	@Override
	public Map<String, String> getFilters() {
		return filters;
	}

	@Override
	public String getValidateDocUpdate() {
		return validateDocUpdate;
	}

	@Override
	public String getLanguage() {
		return "javascript";
	}
}
