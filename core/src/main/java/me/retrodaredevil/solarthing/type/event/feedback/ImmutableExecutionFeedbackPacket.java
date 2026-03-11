package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import org.jspecify.annotations.NonNull;

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
		this.message = requireNonNull(message);
		this.category = requireNonNull(category);
		this.executionReason = requireNonNull(executionReason);
	}

	@Override
	public @NonNull String getMessage() {
		return message;
	}

	@Override
	public @NonNull String getCategory() {
		return category;
	}

	@Override
	public @NonNull ExecutionReason getExecutionReason() {
		return executionReason;
	}
}
