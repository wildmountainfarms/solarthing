package me.retrodaredevil.solarthing.config;

import com.google.gson.JsonObject;
import me.retrodaredevil.influxdb.InfluxProperties;
import me.retrodaredevil.influxdb.InfluxPropertiesBuilder;

public final class JsonInfluxDb {
	private JsonInfluxDb() {
		throw new UnsupportedOperationException();
	}

	public static InfluxProperties getInfluxPropertiesFromJson(JsonObject config) {
		return new InfluxPropertiesBuilder()
			.setUrl(config.get("url").getAsString())
			.setUsername(config.get("username").getAsString())
			.setPassword(config.get("password").getAsString())
			.build();
	}

}
