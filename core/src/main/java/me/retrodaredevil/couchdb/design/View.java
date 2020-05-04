package me.retrodaredevil.couchdb.design;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

@JsonExplicit
public interface View {
	@JsonProperty("map")
	String getMapFunction();
}
