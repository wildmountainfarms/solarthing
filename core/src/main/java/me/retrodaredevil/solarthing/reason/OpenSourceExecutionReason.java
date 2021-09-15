package me.retrodaredevil.solarthing.reason;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.OpenSource;
import me.retrodaredevil.solarthing.annotations.NotNull;

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
}
