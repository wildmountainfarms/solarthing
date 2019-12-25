package me.retrodaredevil.okhttp3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@SuppressWarnings("UnusedReturnValue")
@JsonPOJOBuilder(buildMethodName = "build")
@JsonIgnoreProperties
public class OkHttpPropertiesBuilder implements OkHttpProperties {

	private boolean isRetryOnConnectionFailure = true;
	private int callTimeoutMillis = 0;
	private int connectTimeoutMillis = 10_000;
	private int readTimeoutMillis = 10_000;
	private int writeTimeoutMillis = 10_000;
	private int pingIntervalMillis = 0;

	public OkHttpProperties build(){ return new ImmutableOkHttpProperties(this); }

	@Override
	public boolean isRetryOnConnectionFailure() {
		return isRetryOnConnectionFailure;
	}
	@JsonSetter("retry_on_connection_failure")
	public OkHttpPropertiesBuilder setRetryOnConnectionFailure(boolean isRetryOnConnectionFailure){
		this.isRetryOnConnectionFailure = isRetryOnConnectionFailure;
		return this;
	}

	@Override
	public int getCallTimeoutMillis() {
		return callTimeoutMillis;
	}
	public OkHttpPropertiesBuilder setCallTimeoutMillis(int callTimeoutMillis){
		this.callTimeoutMillis = callTimeoutMillis;
		return this;
	}

	@JsonSetter("call_timeout")
	public OkHttpPropertiesBuilder setCallTimeoutSeconds(float seconds){
		return setCallTimeoutMillis(Math.round(seconds * 1000));
	}

	@Override
	public int getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}
	public OkHttpPropertiesBuilder setConnectTimeoutMillis(int connectTimeoutMillis){
		this.connectTimeoutMillis = connectTimeoutMillis;
		return this;
	}

	@JsonSetter("connect_timeout")
	public OkHttpPropertiesBuilder setConnectTimeoutSeconds(float seconds){
		return setConnectTimeoutMillis(Math.round(seconds * 1000));
	}

	@Override
	public int getReadTimeoutMillis() {
		return readTimeoutMillis;
	}
	public OkHttpPropertiesBuilder setReadTimeoutMillis(int readTimeoutMillis){
		this.readTimeoutMillis = readTimeoutMillis;
		return this;
	}
	@JsonSetter("read_timeout")
	public OkHttpPropertiesBuilder setReadTimeoutSeconds(float seconds){
		return setReadTimeoutMillis(Math.round(seconds * 1000));
	}

	@Override
	public int getWriteTimeoutMillis() {
		return writeTimeoutMillis;
	}
	public OkHttpPropertiesBuilder setWriteTimeoutMillis(int writeTimeoutMillis){
		this.writeTimeoutMillis = writeTimeoutMillis;
		return this;
	}
	@JsonSetter("write_timeout")
	public OkHttpProperties setWriteTimeoutSeconds(float seconds){
		return setWriteTimeoutMillis(Math.round(seconds * 1000));
	}

	@Override
	public int getPingIntervalMillis() {
		return pingIntervalMillis;
	}
	public OkHttpPropertiesBuilder setPingIntervalMillis(int pingIntervalMillis){
		this.pingIntervalMillis = pingIntervalMillis;
		return this;
	}
	@JsonSetter("ping_interval")
	public OkHttpPropertiesBuilder setPingIntervalSeconds(float seconds){
		return setPingIntervalMillis(Math.round(seconds * 1000));
	}
}
