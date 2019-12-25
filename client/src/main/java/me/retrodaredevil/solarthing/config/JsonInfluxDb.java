package me.retrodaredevil.solarthing.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.retrodaredevil.influxdb.InfluxProperties;
import me.retrodaredevil.influxdb.InfluxPropertiesBuilder;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicy;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicySetting;
import me.retrodaredevil.solarthing.util.frequency.FrequentObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.retrodaredevil.util.json.JsonHelper.getOrNull;

public final class JsonInfluxDb {
	private JsonInfluxDb() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	public static InfluxProperties getInfluxPropertiesFromJson(JsonObject config) {
		return new InfluxPropertiesBuilder()
			.setUrl(config.get("url").getAsString())
			.setUsername(config.get("username").getAsString())
			.setPassword(config.get("password").getAsString())
			.build();
	}

	/**
	 *
	 * @param config The root config
	 * @return A list of frequency objects that represent the frequency that {@link RetentionPolicySetting} are used
	 */
	@Deprecated
	public static List<FrequentObject<RetentionPolicySetting>> getRetentionPolicySettings(JsonObject config) {
		JsonElement retentionPolicyElement = config.get("retention_policies");
		if(retentionPolicyElement == null || retentionPolicyElement.isJsonNull()){
			return Collections.emptyList();
		}
		JsonArray retentionPolicyArray = retentionPolicyElement.getAsJsonArray();
		List<FrequentObject<RetentionPolicySetting>> r = new ArrayList<>(retentionPolicyArray.size());
		for(JsonElement element : retentionPolicyArray){
			JsonObject object = element.getAsJsonObject();
			Integer frequency = getOrNull(object, "frequency", JsonElement::getAsInt, true);
			String name = getOrNull(object, "name", JsonElement::getAsString, true);

			String duration = getOrNull(object, "duration", JsonElement::getAsString, true);
			Integer replicationNullable = getOrNull(object, "replication", JsonElement::getAsInt, true);
			int replication = replicationNullable == null ? 1 : replicationNullable;
			String shardDuration = getOrNull(object, "shard_duration", JsonElement::getAsString, true);
			Boolean setAsDefaultNullable = getOrNull(object, "set_as_default", JsonElement::getAsBoolean, true);
			boolean setAsDefault = setAsDefaultNullable == null ? false : setAsDefaultNullable;

			Boolean automaticallyAlterNullable = getOrNull(object, "auto_alter", JsonElement::getAsBoolean, true);
			boolean automaticallyAlter = automaticallyAlterNullable == null ? false : automaticallyAlterNullable;

			Boolean ignoreUnsuccessfulCreateNullable = getOrNull(object, "ignore_unsuccessful_create", JsonElement::getAsBoolean, true);
			boolean ignoreUnsuccessfulCreate = ignoreUnsuccessfulCreateNullable == null ? false : ignoreUnsuccessfulCreateNullable;

			final RetentionPolicySetting setting;
			if(name == null){
				setting = RetentionPolicySetting.DEFAULT_POLICY;
			} else if(duration == null && replicationNullable == null && shardDuration == null && !setAsDefault){
				setting = RetentionPolicySetting.createUnspecifiedRetentionPolicy(name);
				if(automaticallyAlter){
					throw new IllegalArgumentException("Cannot automatically alter name=" + name + " because no retention policy was declared!");
				}
			} else {
				if(duration == null){
					throw new NullPointerException("You must define the duration! We have no default value for that!");
				}
				setting = RetentionPolicySetting.createRetentionPolicy(
					name, true, automaticallyAlter, ignoreUnsuccessfulCreate, // hard code tryToCreate=true for now.
					new RetentionPolicy(duration, replication, shardDuration, setAsDefault)
				);
			}
			r.add(new FrequentObject<>(setting, frequency));
		}
		return r;
	}
}
