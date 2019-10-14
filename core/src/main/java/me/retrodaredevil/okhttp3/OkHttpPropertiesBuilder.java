package me.retrodaredevil.okhttp3;

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

	@Override
	public int getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}
	public OkHttpPropertiesBuilder setConnectTimeoutMillis(int connectTimeoutMillis){
		this.connectTimeoutMillis = connectTimeoutMillis;
		return this;
	}

	@Override
	public int getReadTimeoutMillis() {
		return readTimeoutMillis;
	}
	public OkHttpPropertiesBuilder setReadTimeoutMillis(int readTimeoutMillis){
		this.readTimeoutMillis = readTimeoutMillis;
		return this;
	}

	@Override
	public int getWriteTimeoutMillis() {
		return writeTimeoutMillis;
	}
	public OkHttpPropertiesBuilder setWriteTimeoutMillis(int writeTimeoutMillis){
		this.writeTimeoutMillis = writeTimeoutMillis;
		return this;
	}

	@Override
	public int getPingIntervalMillis() {
		return pingIntervalMillis;
	}
	public OkHttpPropertiesBuilder setPingIntervalMillis(int pingIntervalMillis){
		this.pingIntervalMillis = pingIntervalMillis;
		return this;
	}
}
