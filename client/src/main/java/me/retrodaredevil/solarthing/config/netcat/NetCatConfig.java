package me.retrodaredevil.solarthing.config.netcat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetCatConfig {
	private final String bindAddress;
	private final int port;

	public NetCatConfig(
			@JsonProperty("bind") String bindAddress,
			@JsonProperty(value = "port", required = true) int port) {
		this.bindAddress = bindAddress == null ? "localhost" : bindAddress;
		this.port = port;
	}

	public String getBindAddress() {
		return bindAddress;
	}

	public int getPort() {
		return port;
	}
}
