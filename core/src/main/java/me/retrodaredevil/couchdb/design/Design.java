package me.retrodaredevil.couchdb.design;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.util.Map;

@JsonExplicit
public interface Design {
	@JsonProperty("language")
	String getLanguage();
	@JsonProperty("views")
	Map<String, View> getViews();
}
