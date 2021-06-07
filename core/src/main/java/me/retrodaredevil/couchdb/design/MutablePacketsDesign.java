package me.retrodaredevil.couchdb.design;

import java.util.HashMap;
import java.util.Map;

public class MutablePacketsDesign implements Design {
	private final Map<String, View> views = new HashMap<>();

	public MutablePacketsDesign addMillisView() {
		views.put("millis", new SimpleView("function(doc) {\n  emit(doc.dateMillis, doc);\n}"));
		return this;
	}
	public MutablePacketsDesign addReadonlyAuth() {
		String function = "function(newDoc, oldDoc, userCtx, secObj) {\n\n  secObj.admins = secObj.admins || {};\n  secObj.admins.names = secObj.admins.names || [];\n  secObj.admins.roles = secObj.admins.roles || [];\n\n  var isAdmin = false;\n  if(userCtx.roles.indexOf('_admin') !== -1) {\n    isAdmin = true;\n  }\n  if(secObj.admins.names.indexOf(userCtx.name) !== -1) {\n    isAdmin = true;\n  }\n  for(var i = 0; i < userCtx.roles; i++) {\n    if(secObj.admins.roles.indexOf(userCtx.roles[i]) !== -1) {\n      isAdmin = true;\n    }\n  }\n\n  if(!isAdmin) {\n    throw {'unauthorized':'This is read only when unauthorized'};\n  }\n}";
		views.put("readonly_auth", new SimpleView(function));
		return this;
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
