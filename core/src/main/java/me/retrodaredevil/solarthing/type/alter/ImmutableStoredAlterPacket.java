package me.retrodaredevil.solarthing.type.alter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ImmutableStoredAlterPacket implements StoredAlterPacket {
	private final String databaseId;
	private final long updatedDateMillis;
	private final AlterPacket packet;
	private final String sourceId;

	@JsonCreator
	public ImmutableStoredAlterPacket(
			@JsonProperty(value = "_id", required = true) String databaseId,
			@JsonProperty(value = "updatedDateMillis", required = true) long updatedDateMillis,
			@JsonProperty(value = "packet", required = true) AlterPacket packet,
			@JsonProperty(value = "sourceId", required = true) String sourceId) {
		this.databaseId = requireNonNull(databaseId);
		this.updatedDateMillis = updatedDateMillis;
		this.packet = requireNonNull(packet);
		this.sourceId = requireNonNull(sourceId);
	}

	@Override
	public String getDbId() {
		return databaseId;
	}

	@Override
	public long getUpdatedDateMillis() {
		return updatedDateMillis;
	}

	@Override
	public AlterPacket getPacket() {
		return packet;
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}
}
