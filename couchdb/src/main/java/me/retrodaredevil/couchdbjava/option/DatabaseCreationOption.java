package me.retrodaredevil.couchdbjava.option;

public class DatabaseCreationOption {

	private static final DatabaseCreationOption DEFAULT_OPTION = new DatabaseCreationOption(null, null, null);

	private final Integer shards;
	private final Integer replicas;
	private final Boolean partitioned;

	public DatabaseCreationOption(Integer shards, Integer replicas, Boolean partitioned) {
		this.shards = shards;
		this.replicas = replicas;
		this.partitioned = partitioned;
	}
	public static DatabaseCreationOption createDefault() {
		return DEFAULT_OPTION;
	}

	public Integer getShards() {
		return shards;
	}

	public Integer getReplicas() {
		return replicas;
	}

	public Boolean getPartitioned() {
		return partitioned;
	}
}
