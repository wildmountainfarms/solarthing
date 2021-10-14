package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.convenient.SingleActionNode;
import me.retrodaredevil.action.node.convenient.TimeoutActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

@JsonSubTypes({
		@JsonSubTypes.Type(QueueActionNode.class),
		@JsonSubTypes.Type(WaitMillisActionNode.class),
		@JsonSubTypes.Type(WaitIsoActionNode.class),
		@JsonSubTypes.Type(CallActionNode.class),
		@JsonSubTypes.Type(PassActionNode.class),
		@JsonSubTypes.Type(LockActionNode.class),
		@JsonSubTypes.Type(UnlockActionNode.class),
		@JsonSubTypes.Type(DeclarationActionNode.class),
		@JsonSubTypes.Type(RaceActionNode.class),
		@JsonSubTypes.Type(TimeoutActionNode.class),
		@JsonSubTypes.Type(SingleActionNode.class),
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
