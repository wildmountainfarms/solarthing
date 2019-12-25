package me.retrodaredevil.solarthing.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.okhttp3.OkHttpPropertiesBuilder;

public final class JsonOkHttp {
	private JsonOkHttp(){ throw new UnsupportedOperationException(); }
	@Deprecated
	public static OkHttpProperties getOkHttpPropertiesFromJson(JsonObject config) {
		OkHttpPropertiesBuilder builder = new OkHttpPropertiesBuilder();

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
