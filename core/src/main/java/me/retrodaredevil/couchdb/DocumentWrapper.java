package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

/**
 * A simple class that wraps an object. This can be used with the Ektorp library and in CouchDB applications that use Jackson.
 */
@JsonExplicit
@Deprecated
public class DocumentWrapper {
	private final String id;
	private String rev = null;
	private Object object = null;

	public DocumentWrapper(String id) {
		this.id = id;
	}

	@JsonGetter("_id")
	public String getId(){
		return id;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonGetter("_rev")
	public String getRevision(){
		return rev;
	}
	@JsonSetter("_rev")
	public void setRevision(String rev){
		this.rev = rev;
	}
	@JsonUnwrapped
	public Object getObject(){
		return object;
	}
	public void setObject(Object object){
		this.object = object;
	}
}
