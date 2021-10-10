package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.solarthing.actions.chatbot.SlackChatBotActionNode;
import me.retrodaredevil.solarthing.actions.command.AlterManagerActionNode;
import me.retrodaredevil.solarthing.actions.command.ExecutingCommandFeedbackActionNode;
import me.retrodaredevil.solarthing.actions.command.SendCommandActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.homeassistant.HomeAssistantActionNode;
import me.retrodaredevil.solarthing.actions.mate.*;
import me.retrodaredevil.solarthing.actions.message.MessageSenderActionNode;
import me.retrodaredevil.solarthing.actions.rover.RoverBoostSetActionNode;
import me.retrodaredevil.solarthing.actions.rover.RoverBoostVoltageActionNode;
import me.retrodaredevil.solarthing.actions.rover.RoverLoadActionNode;
import me.retrodaredevil.solarthing.actions.solcast.SolcastActionNode;
import me.retrodaredevil.solarthing.actions.tracer.TracerLoadActionNode;

@JsonSubTypes({
		@JsonSubTypes.Type(QueueActionNode.class),
		@JsonSubTypes.Type(WaitMillisActionNode.class),
		@JsonSubTypes.Type(WaitIsoActionNode.class),
		@JsonSubTypes.Type(CallActionNode.class),
		@JsonSubTypes.Type(LogActionNode.class),
		@JsonSubTypes.Type(PassActionNode.class),
		@JsonSubTypes.Type(LockActionNode.class),
		@JsonSubTypes.Type(UnlockActionNode.class),
		@JsonSubTypes.Type(DeclarationActionNode.class),
		@JsonSubTypes.Type(RaceActionNode.class),
		@JsonSubTypes.Type(TimeoutActionNode.class),

		@JsonSubTypes.Type(RequiredIdentifierActionNode.class),
		@JsonSubTypes.Type(RequireFullOutputActionNode.class),

		@JsonSubTypes.Type(ACModeActionNode.class),
		@JsonSubTypes.Type(AuxStateActionNode.class),
		@JsonSubTypes.Type(FXOperationalModeActionNode.class),
		@JsonSubTypes.Type(MateCommandActionNode.class),
		@JsonSubTypes.Type(MateCommandWaitActionNode.class),

		@JsonSubTypes.Type(RoverLoadActionNode.class),
		@JsonSubTypes.Type(RoverBoostSetActionNode.class),
		@JsonSubTypes.Type(RoverBoostVoltageActionNode.class),

		@JsonSubTypes.Type(TracerLoadActionNode.class),

		@JsonSubTypes.Type(SendCommandActionNode.class),

		@JsonSubTypes.Type(HomeAssistantActionNode.class),
		@JsonSubTypes.Type(SolcastActionNode.class),

		@JsonSubTypes.Type(MessageSenderActionNode.class),

		@JsonSubTypes.Type(SlackChatBotActionNode.class),

		@JsonSubTypes.Type(ExecutingCommandFeedbackActionNode.class),

		@JsonSubTypes.Type(AlterManagerActionNode.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public interface ActionNode {
	/**
	 * Creates an {@link Action} gives an {@link ActionEnvironment}.
	 * <p>
	 * Note: You are allowed to call this method from a separate thread, so make sure your implementation of this method is thread safe should it access
	 * mutating state. Note that you DO NOT have to make the returned {@link Action} thread safe.
	 * @param actionEnvironment The action environment to create the action
	 * @return The action
	 */
	Action createAction(ActionEnvironment actionEnvironment);
}
