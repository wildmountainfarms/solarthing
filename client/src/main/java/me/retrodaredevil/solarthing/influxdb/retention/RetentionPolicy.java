package me.retrodaredevil.solarthing.influxdb.retention;

import static java.util.Objects.requireNonNull;

public final class RetentionPolicy {
	private final String duration;
	private final Integer replication;
	private final String shardDuration;
	private final boolean setAsDefault;

	public RetentionPolicy(String duration, Integer replication, String shardDuration, boolean setAsDefault) {
		this.duration = duration;
		this.replication = replication;
		this.shardDuration = shardDuration;
		this.setAsDefault = setAsDefault;
		if(duration == null && replication == null && shardDuration == null && !setAsDefault){
			throw new IllegalArgumentException("A retention policy must have DURATION, REPLICATION, SHARD DURATION, or DEFAULT declared!");
		}
	}

	public String getDuration(){ return duration; }
	public Integer getReplication(){ return replication; }
	public String getShardDuration(){ return shardDuration; }
	public boolean isSetAsDefault(){ return setAsDefault; }

	public String toPolicyString(String policyName, String databaseName){
		String r = "RETENTION POLICY \"" + requireNonNull(policyName) + "\" ON \"" + requireNonNull(databaseName) + "\"";
		boolean valid = false;
		if(duration != null){
			r += " DURATION " + duration;
			valid = true;
		}
		if(replication != null){
			r += " REPLICATION " + replication;
			valid = true;
		}
		if(shardDuration != null){
			r += " SHARD DURATION " + shardDuration;
			valid = true;
		}
		if(setAsDefault){
			r += " DEFAULT";
			valid = true;
		}
		if(!valid){
			throw new AssertionError("This retention policy is not in a valid state to create its policy string! We should have checked for this... r=" + r);
		}
		return r;
	}
}
