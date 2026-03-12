package me.retrodaredevil.solarthing.reason;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@JsonTypeName("PACKET_COLLECTION")
@NullMarked
public class PacketCollectionExecutionReason implements ExecutionReason {
	private final long dateMillis;
	private final String dbId;

	@JsonCreator
	public PacketCollectionExecutionReason(
			@JsonProperty(value = "dateMillis", required = true) long dateMillis,
			@JsonProperty(value = "dbId", required = true) String dbId
	) {
		this.dateMillis = dateMillis;
		this.dbId = requireNonNull(dbId);
	}

	// TODO remove NonNull
	@Override
	public @NonNull ExecutionReasonType getPacketType() {
		return ExecutionReasonType.PACKET_COLLECTION;
	}

	@Override
	public String getUniqueString() {
		return "PacketCollectionExecutionReason(dateMillis=" + dateMillis + ")";
	}

	@JsonProperty("dateMillis")
	public long getDateMillis() {
		return dateMillis;
	}

	@JsonProperty("dbId")
	public String getDbId() {
		return dbId;
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PacketCollectionExecutionReason that = (PacketCollectionExecutionReason) o;
		return dateMillis == that.dateMillis && dbId.equals(that.dbId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dateMillis, dbId);
	}
}
