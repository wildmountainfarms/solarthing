package me.retrodaredevil.solarthing.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.retrodaredevil.influxdb.InfluxProperties;
import me.retrodaredevil.influxdb.InfluxPropertiesBuilder;

public final class JsonInfluxDb {
	private JsonInfluxDb() {
		throw new UnsupportedOperationException();
	}

	public static InfluxProperties getInfluxPropertiesFromJson(JsonObject config) {
		InfluxPropertiesBuilder builder = new InfluxPropertiesBuilder()
			.setUrl(config.get("url").getAsString())
			.setUsername(config.get("username").getAsString())
			.setPassword(config.get("password").getAsString());

		JsonElement retryOnFailure = config.get("retry_on_connection_failure");
		if(retryOnFailure != null) builder.setRetryOnConnectionFailure(retryOnFailure.getAsBoolean());
		JsonElement callTimeout = config.get("call_timeout");
		if(callTimeout != null) builder.setCallTimeoutMillis((int) (callTimeout.getAsFloat() * 1000));
		JsonElement connectionTimeout = config.get("connection_timeout");
		if(connectionTimeout != null) builder.setConnectTimeoutMillis((int) (connectionTimeout.getAsFloat() * 1000));
		JsonElement readTimeout = config.get("read_timeout");
		if(readTimeout != null) builder.setReadTimeoutMillis((int) (readTimeout.getAsFloat() * 1000));
		JsonElement writeTimeout = config.get("write_timeout");
		if(writeTimeout != null) builder.setWriteTimeoutMillis((int) (writeTimeout.getAsFloat() * 1000));
		JsonElement pingInterval = config.get("ping_interval");
		if(pingInterval != null) builder.setPingIntervalMillis((int) (pingInterval.getAsFloat() * 1000));
		return builder.build();
	}

}
