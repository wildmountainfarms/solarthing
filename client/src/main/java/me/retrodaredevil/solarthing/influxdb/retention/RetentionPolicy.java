package me.retrodaredevil.solarthing.influxdb.retention;

import static java.util.Objects.requireNonNull;

public final class RetentionPolicy {
	private final String duration;
	private final int replication;
	private final String shardDuration;
	private final boolean setAsDefault;

	public RetentionPolicy(String duration, int replication, String shardDuration, boolean setAsDefault) {
		this.duration = requireNonNull(duration);
		this.replication = replication;
		this.shardDuration = shardDuration;
		this.setAsDefault = setAsDefault;
	}

	public String getDuration(){ return duration; }
	public int getReplication(){ return replication; }
	public String getShardDuration(){ return shardDuration; }
	public boolean isSetAsDefault(){ return setAsDefault; }

	public String toPolicyStringInfluxDb1(String policyName, String databaseName){
		String r = "RETENTION POLICY \"" + requireNonNull(policyName) + "\" ON \"" + requireNonNull(databaseName) + "\""
			+ (" DURATION " + duration)
			+ (" REPLICATION " + replication);
		if(shardDuration != null){
			r += " SHARD DURATION " + shardDuration;
		}
		if(setAsDefault){
			r += " DEFAULT";
		}
		return r;
	}
}
