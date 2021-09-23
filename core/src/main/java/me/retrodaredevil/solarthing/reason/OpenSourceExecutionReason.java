package me.retrodaredevil.solarthing.reason;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.type.open.OpenSource;
import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@JsonTypeName("SOURCE")
public class OpenSourceExecutionReason implements ExecutionReason {
	private final OpenSource source;

	@JsonCreator
	public OpenSourceExecutionReason(@JsonProperty(value = "source", required = true) OpenSource source) {
		requireNonNull(this.source = source);
	}

	@Override
	public @NotNull ExecutionReasonType getPacketType() {
		return ExecutionReasonType.SOURCE;
	}

	@JsonProperty("source")
	public @NotNull OpenSource getSource() {
		return source;
	}

	@Override
	public @NotNull String getUniqueString() {
		return "OpenSourceExecutionReason(source=" + source.getUniqueString() + ")";
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public boolean equals(Object o) {
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
