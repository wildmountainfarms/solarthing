package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import static java.util.Objects.requireNonNull;

/**
 * https://docs.couchdb.org/en/stable/api/database/common.html
 */
@SuppressWarnings("unused")
public class DatabaseInfo {
	private final String name;
	private final String purgeSequence;
	private final String updateSequence;

	private final Sizes sizes;

	private final Properties properties;
	private final int documentDeleteCount;
	private final int documentCount;
	private final int diskFormatVersion;
	private final boolean compactRunning;

	private final Cluster cluster;

	private final String instanceStartTime;

	public DatabaseInfo(
			@JsonProperty(value = "db_name", required = true) String name,
			@JsonProperty(value = "purge_seq", required = true) String purgeSequence, @JsonProperty(value = "update_seq", required = true) String updateSequence,
			@JsonProperty(value = "sizes", required = true) Sizes sizes,
			@JsonProperty(value = "properties", required = true) Properties properties,
			@JsonProperty(value = "doc_del_count", required = true) int documentDeleteCount,
			@JsonProperty(value = "doc_count", required = true) int documentCount,
			@JsonProperty(value = "disk_format_version", required = true) int diskFormatVersion,
			@JsonProperty(value = "compact_running", required = true) boolean compactRunning,
			@JsonProperty(value = "cluster", required = true) Cluster cluster,
			@JsonProperty(value = "instance_start_time", required = true) String instanceStartTime) {
		requireNonNull(this.name = name);
		requireNonNull(this.purgeSequence = purgeSequence);
		requireNonNull(this.updateSequence = updateSequence);
		requireNonNull(this.sizes = sizes);
		requireNonNull(this.properties = properties);
		this.documentDeleteCount = documentDeleteCount;
		this.documentCount = documentCount;
		this.diskFormatVersion = diskFormatVersion;
		this.compactRunning = compactRunning;
		requireNonNull(this.cluster = cluster);
		requireNonNull(this.instanceStartTime = instanceStartTime);
		if (!instanceStartTime.equals("0")) {
			throw new IllegalArgumentException("The instance_start_time must be 0! It is: " + instanceStartTime);
		}
	}
	@JsonValue
	private String _jacksonValue() {
		throw new UnsupportedOperationException("DatabaseInfo cannot be serialized!");
	}

	public String getName() {
		return name;
	}

	public String getPurgeSequence() {
		return purgeSequence;
	}

	public String getUpdateSequence() {
		return updateSequence;
	}

	public Sizes getSizes() {
		return sizes;
	}

	public Properties getProperties() {
		return properties;
	}

	public int getDocumentDeleteCount() {
		return documentDeleteCount;
	}

	public int getDocumentCount() {
		return documentCount;
	}

	public int getDiskFormatVersion() {
		return diskFormatVersion;
	}

	public boolean isCompactRunning() {
		return compactRunning;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public String getInstanceStartTime() {
		return instanceStartTime;
	}

	public static class Sizes {
		private final long file, external, active;

		public Sizes(
				@JsonProperty(value = "file", required = true) long file,
				@JsonProperty(value = "external", required = true) long external,
				@JsonProperty(value = "active", required = true) long active) {
			this.file = file;
			this.external = external;
			this.active = active;
		}

		public long getFile() {
			return file;
		}

		public long getExternal() {
			return external;
		}

		public long getActive() {
			return active;
		}
		@JsonValue
		private String _jacksonValue() {
			throw new UnsupportedOperationException("Sizes cannot be serialized!");
		}
	}
	public static class Properties {

		private final boolean partitioned;

		public Properties(@JsonProperty("partitioned") Boolean partitioned) {
			this.partitioned = Boolean.TRUE.equals(partitioned);
		}

		public boolean isPartitioned() {
			return partitioned;
		}
		@JsonValue
		private String _jacksonValue() {
			throw new UnsupportedOperationException("Properties cannot be serialized!");
		}
	}
	public static class Cluster {
		private final int shards; // q
		private final int replicas; // n
		private final int writeQuorum; // w
		private final int readQuorum; // r

		public Cluster(
				@JsonProperty(value = "q", required = true) int shards,
				@JsonProperty(value = "n", required = true) int replicas,
				@JsonProperty(value = "w", required = true) int writeQuorum,
				@JsonProperty(value = "r", required = true) int readQuorum) {
			this.shards = shards;
			this.replicas = replicas;
			this.writeQuorum = writeQuorum;
			this.readQuorum = readQuorum;
		}

		public int getShards() {
			return shards;
		}

		public int getReplicas() {
			return replicas;
		}

		public int getWriteQuorum() {
			return writeQuorum;
		}

		public int getReadQuorum() {
			return readQuorum;
		}
		@JsonValue
		private String _jacksonValue() {
			throw new UnsupportedOperationException("Cluster cannot be serialized!");
		}
	}
}
