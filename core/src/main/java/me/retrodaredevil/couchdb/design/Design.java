package me.retrodaredevil.couchdb.design;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.util.Map;

@JsonExplicit
public interface Design {
	@JsonProperty("language")
	String getLanguage();
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	@JsonProperty("views")
	Map<String, View> getViews();

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("validate_doc_update")
	String getValidateDocUpdate();
}
