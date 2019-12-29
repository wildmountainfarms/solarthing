package me.retrodaredevil.solarthing.influxdb.retention;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = RetentionPolicySetting.Builder.class)
public final class RetentionPolicySetting {
	/**
	 * This corresponds to a default retention policy that is automatically determined when you put a point in a database without specifying a retention policy.
	 */
	public static final RetentionPolicySetting DEFAULT_POLICY = new RetentionPolicySetting(null, false, false, false, null);

	private final String name;
	private final boolean tryToCreate;
	private final boolean automaticallyAlter;
	private final boolean ignoreUnsuccessfulCreate;
	private final RetentionPolicy retentionPolicy;

	private RetentionPolicySetting(String name, boolean tryToCreate, boolean automaticallyAlter, boolean ignoreUnsuccessfulCreate, RetentionPolicy retentionPolicy) {
		this.name = name;
		this.tryToCreate = tryToCreate;
		this.automaticallyAlter = automaticallyAlter;
		this.ignoreUnsuccessfulCreate = ignoreUnsuccessfulCreate;
		this.retentionPolicy = retentionPolicy;
		if(retentionPolicy == null && (tryToCreate || automaticallyAlter || ignoreUnsuccessfulCreate)){
			throw new IllegalArgumentException("Cannot automatically create or alter a retention policy if retentionPolicy is null! tryToCreate=" + tryToCreate + " automaticallyAlter=" + automaticallyAlter + " ignoreUnsuccessfulCreate=" + ignoreUnsuccessfulCreate);
		}
		if(!tryToCreate && ignoreUnsuccessfulCreate){
			throw new IllegalArgumentException("Cannot ignore an unsuccessful create if we aren't even going to try to create!");
		}
		if(automaticallyAlter && ignoreUnsuccessfulCreate){
			throw new IllegalArgumentException("Cannot automatically alter and ignore an unsuccessful create at the same time!");
		}
	}
	public static RetentionPolicySetting createRetentionPolicy(String name, boolean tryToCreate, boolean automaticallyAlter, boolean ignoreUnsuccessfulCreate, RetentionPolicy retentionPolicy) {
		return new RetentionPolicySetting(name, tryToCreate, automaticallyAlter, ignoreUnsuccessfulCreate, retentionPolicy);
	}
	public static RetentionPolicySetting createUnspecifiedRetentionPolicy(String name){
		return new RetentionPolicySetting(name, false, false, false, null);
	}

	public String getName() {
		return name;
	}

	public boolean isTryToCreate() {
		return tryToCreate;
	}

	public boolean isAutomaticallyAlter() {
		return automaticallyAlter;
	}

	public RetentionPolicy getRetentionPolicy() {
		return retentionPolicy;
	}

	public boolean isIgnoreUnsuccessfulCreate() {
		return ignoreUnsuccessfulCreate;
	}

	@JsonPOJOBuilder
	static class Builder {
		@JsonProperty("name")
		private String name;
		@JsonProperty("auto_alter")
		private Boolean automaticallyAlterNullable = null;
		@JsonProperty("ignore_unsuccessful_create")
		private Boolean ignoreUnsuccessfulCreateNullable = null;

		@JsonProperty("duration")
		private String duration = null;
		@JsonProperty("replication")
		private Integer replicationNullable = null;
		@JsonProperty("shard_duration")
		private String shardDuration = null;
		@JsonProperty("set_as_default")
		private Boolean setAsDefaultNullable = null;

		public RetentionPolicySetting build() {
			int replication = replicationNullable == null ? 1 : replicationNullable;
			boolean setAsDefault = setAsDefaultNullable == null ? false : setAsDefaultNullable;
			boolean automaticallyAlter = automaticallyAlterNullable == null ? false : automaticallyAlterNullable;
			boolean ignoreUnsuccessfulCreate = ignoreUnsuccessfulCreateNullable == null ? false : ignoreUnsuccessfulCreateNullable;

			if(name == null){
				return RetentionPolicySetting.DEFAULT_POLICY;
			} else if(duration == null && replicationNullable == null && shardDuration == null && !setAsDefault){
				if(automaticallyAlter){
					throw new IllegalArgumentException("Cannot automatically alter name=" + name + " because no retention policy was declared!");
				}
				return createUnspecifiedRetentionPolicy(name);
			}
			if(duration == null){
				throw new NullPointerException("You must define the duration! We have no default value for that!");
			}
			return createRetentionPolicy(
					name, true, automaticallyAlter, ignoreUnsuccessfulCreate, // hard code tryToCreate=true for now.
					new RetentionPolicy(duration, replication, shardDuration, setAsDefault)
			);
		}
	}
}
