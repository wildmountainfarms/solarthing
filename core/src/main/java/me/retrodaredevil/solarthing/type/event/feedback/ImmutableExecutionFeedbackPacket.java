package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.reason.ExecutionReason;

import static java.util.Objects.requireNonNull;

public class ImmutableExecutionFeedbackPacket implements ExecutionFeedbackPacket {
	private final String message;
	private final String category;
	private final ExecutionReason executionReason;

	@JsonCreator
	public ImmutableExecutionFeedbackPacket(
			@JsonProperty(value = "message", required = true) String message,
			@JsonProperty(value = "category", required = true) String category,
			@JsonProperty(value = "executionReason", required = true) ExecutionReason executionReason) {
		requireNonNull(this.message = message);
		requireNonNull(this.category = category);
		requireNonNull(this.executionReason = executionReason);
	}

	@Override
	public @NotNull String getMessage() {
		return message;
	}

	@Override
	public @NotNull String getCategory() {
		return category;
	}

	@Override
	public @NotNull ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
