package me.retrodaredevil.solarthing.config;

import com.google.gson.JsonObject;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;

public final class JsonCouchDb {
	private JsonCouchDb(){ throw new UnsupportedOperationException(); }
	
	public static CouchProperties getCouchPropertiesFromJson(JsonObject jsonObject){
		CouchPropertiesBuilder builder = new CouchPropertiesBuilder(null, false, "http", null, -1, null, null);
		builder.setHost(jsonObject.getAsJsonPrimitive("host").getAsString());
		builder.setPort(jsonObject.getAsJsonPrimitive("port").getAsInt());
		// TODO set more options based on json object
		return builder.build();
	}
}
