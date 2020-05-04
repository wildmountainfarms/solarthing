package me.retrodaredevil.couchdb.design;

import java.util.HashMap;
import java.util.Map;

public class DefaultPacketsDesign implements Design {
	private final Map<String, View> views = new HashMap<>();
	{
		views.put("millis", new SimpleView("function(doc) {\n  emit(doc.dateMillis, doc);\n}"));
	}
	@Override
	public Map<String, View> getViews() {
		return views;
	}

	@Override
	public String getLanguage() {
		return "javascript";
	}
}
