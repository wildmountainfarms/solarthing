package me.retrodaredevil.couchdb.design;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NullMarked;

@JsonExplicit
@NullMarked
public interface View {
	@JsonProperty("map")
	String getMapFunction();
}
