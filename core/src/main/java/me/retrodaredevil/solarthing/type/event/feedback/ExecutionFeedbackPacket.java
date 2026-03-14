package me.retrodaredevil.solarthing.type.event.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import org.jspecify.annotations.NullMarked;

@JsonDeserialize(as = ImmutableExecutionFeedbackPacket.class)
@JsonTypeName("EXECUTION_FEEDBACK")
@NullMarked
public interface ExecutionFeedbackPacket extends FeedbackPacket {
	@DefaultFinal
	@Override
	default FeedbackPacketType getPacketType() {
		return FeedbackPacketType.EXECUTION_FEEDBACK;
	}

	/**
	 * @return The message of this feedback packet. This can be displayed to people
	 */
	@JsonProperty("message")
	String getMessage();

	/**
	 * The recommended format of this is: "some_category.sub_category"
	 * @return The category of this message. This purpose of this value is up for you to decide. Its intent is to be used to filter messages
	 */
	@JsonProperty("category")
	String getCategory();

	@JsonProperty("executionReason")
	ExecutionReason getExecutionReason();

	// TODO consider adding a Nullable error type field here -- maybe WARN and ERROR
}
