package me.retrodaredevil.solarthing.influxdb.retention;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class RetentionPolicy {
	private final String duration;
	private final int replication;
	private final @Nullable String shardDuration;
	private final boolean setAsDefault;

	public RetentionPolicy(String duration, int replication, @Nullable String shardDuration, boolean setAsDefault) {
		this.duration = requireNonNull(duration);
		this.replication = replication;
		this.shardDuration = shardDuration;
		this.setAsDefault = setAsDefault;
	}

	public String getDuration(){ return duration; }
	public int getReplication(){ return replication; }
	public @Nullable String getShardDuration(){ return shardDuration; }
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
