package me.retrodaredevil.okhttp3;

public class ImmutableOkHttpProperties implements OkHttpProperties {
	private final boolean isRetryOnConnectionFailure;
	private final int callTimeoutMillis;
	private final int connectTimeoutMillis;
	private final int readTimeoutMillis;
	private final int writeTimeoutMillis;
	private final int pingIntervalMillis;
	public ImmutableOkHttpProperties(OkHttpProperties okHttpProperties){
		isRetryOnConnectionFailure = okHttpProperties.isRetryOnConnectionFailure();
		callTimeoutMillis = okHttpProperties.getCallTimeoutMillis();
		connectTimeoutMillis = okHttpProperties.getConnectTimeoutMillis();
		readTimeoutMillis = okHttpProperties.getReadTimeoutMillis();
		writeTimeoutMillis = okHttpProperties.getWriteTimeoutMillis();
		pingIntervalMillis = okHttpProperties.getPingIntervalMillis();
	}
	@Override
	public boolean isRetryOnConnectionFailure() {
		return isRetryOnConnectionFailure;
	}

	@Override
	public int getCallTimeoutMillis() {
		return callTimeoutMillis;
	}

	@Override
	public int getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}

	@Override
	public int getReadTimeoutMillis() {
		return readTimeoutMillis;
	}

	@Override
	public int getWriteTimeoutMillis() {
		return writeTimeoutMillis;
	}

	@Override
	public int getPingIntervalMillis() {
		return pingIntervalMillis;
	}
}
