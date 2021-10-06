package me.retrodaredevil.solarthing.type.alter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

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
		requireNonNull(this.databaseId = databaseId);
		this.updatedDateMillis = updatedDateMillis;
		requireNonNull(this.packet = packet);
		requireNonNull(this.sourceId = sourceId);
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
	public @NotNull AlterPacket getPacket() {
		return packet;
	}

	@Override
	public @NotNull String getSourceId() {
		return sourceId;
	}
}
