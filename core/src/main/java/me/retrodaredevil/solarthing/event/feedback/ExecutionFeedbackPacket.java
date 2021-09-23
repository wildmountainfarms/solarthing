package me.retrodaredevil.solarthing.event.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.reason.ExecutionReason;

@JsonTypeName("EXECUTION_FEEDBACK")
public interface ExecutionFeedbackPacket extends FeedbackPacket {
	@Override
	default @NotNull FeedbackPacketType getPacketType() {
		return FeedbackPacketType.EXECUTION_FEEDBACK;
	}

	/**
	 * @return The message of this feedback packet. This can be displayed to people
	 */
	@JsonProperty("message")
	@NotNull String getMessage();

	/**
	 * The recommended format of this is: "some_category.sub_category"
	 * @return The category of this message. This purpose of this value is up for you to decide. Its intent is to be used to filter messages
	 */
	@JsonProperty("category")
	@NotNull String getCategory();

	@JsonProperty("executionReason")
	@NotNull ExecutionReason getExecutionReason();
}
