package me.retrodaredevil.solarthing.reason;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.type.open.OpenSource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@JsonTypeName("SOURCE")
@NullMarked
public class OpenSourceExecutionReason implements ExecutionReason {
	private final OpenSource source;

	@JsonCreator
	public OpenSourceExecutionReason(@JsonProperty(value = "source", required = true) OpenSource source) {
		this.source = requireNonNull(source);
	}

	// TODO remove NonNull
	@Override
	public @NonNull ExecutionReasonType getPacketType() {
		return ExecutionReasonType.SOURCE;
	}

	@JsonProperty("source")
	public OpenSource getSource() {
		return source;
	}

	@Override
	public String getUniqueString() {
		return "OpenSourceExecutionReason(source=" + source.getUniqueString() + ")";
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OpenSourceExecutionReason that = (OpenSourceExecutionReason) o;
		return source.equals(that.source);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source);
	}
}
