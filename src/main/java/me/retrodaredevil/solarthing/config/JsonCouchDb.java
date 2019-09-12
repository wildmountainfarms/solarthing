package me.retrodaredevil.solarthing.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;

public final class JsonCouchDb {
	private JsonCouchDb(){ throw new UnsupportedOperationException(); }
	
	public static CouchProperties getCouchPropertiesFromJson(JsonObject jsonObject){
		CouchPropertiesBuilder builder = new CouchPropertiesBuilder(null, false, "http", null, -1, null, null);
		builder.setProtocol(jsonObject.get("protocol").getAsString());
		builder.setHost(jsonObject.get("host").getAsString());
		builder.setPort(jsonObject.get("port").getAsInt());
		JsonElement usernameElement = jsonObject.get("username");
		JsonElement passwordElement = jsonObject.get("password");
		boolean usernameNull = usernameElement == null || usernameElement.isJsonNull();
		boolean passwordNull = passwordElement == null || passwordElement.isJsonNull();
		if(usernameNull != passwordNull){
			throw new IllegalArgumentException("Both username and password must be either null or both must be defined");
		}
		if(usernameNull){
			builder.setUsername(null).setPassword(null);
		} else {
			builder.setUsername(usernameElement.getAsString()).setPassword(passwordElement.getAsString());
		}
		JsonElement connectionTimeoutElement = jsonObject.get("connection_timeout");
		if(connectionTimeoutElement != null && !connectionTimeoutElement.isJsonNull()){
			builder.setConnectionTimeout(connectionTimeoutElement.getAsInt());
		}
		JsonElement socketTimeoutElement = jsonObject.get("socket_timeout");
		if(socketTimeoutElement != null && !socketTimeoutElement.isJsonNull()){
			builder.setSocketTimeout(socketTimeoutElement.getAsInt());
		}
		return builder.build();
	}
}
