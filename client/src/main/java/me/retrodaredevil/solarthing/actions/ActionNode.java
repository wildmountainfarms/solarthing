package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.mate.ACModeActionNode;
import me.retrodaredevil.solarthing.actions.mate.AuxStateActionNode;
import me.retrodaredevil.solarthing.actions.mate.MateCommandActionNode;
import me.retrodaredevil.solarthing.actions.mate.MateCommandWaitActionNode;
import me.retrodaredevil.solarthing.actions.rover.RoverLoadActionNode;

@JsonSubTypes({
		@JsonSubTypes.Type(QueueActionNode.class),
		@JsonSubTypes.Type(WaitActionNode.class),
		@JsonSubTypes.Type(CallActionNode.class),
		@JsonSubTypes.Type(LogActionNode.class),
		@JsonSubTypes.Type(PassActionNode.class),
		@JsonSubTypes.Type(LockActionNode.class),
		@JsonSubTypes.Type(UnlockActionNode.class),
		@JsonSubTypes.Type(DeclarationActionNode.class),
		@JsonSubTypes.Type(RaceActionNode.class),

		@JsonSubTypes.Type(ACModeActionNode.class),
		@JsonSubTypes.Type(AuxStateActionNode.class),
		@JsonSubTypes.Type(MateCommandActionNode.class),
		@JsonSubTypes.Type(MateCommandWaitActionNode.class),

		@JsonSubTypes.Type(RoverLoadActionNode.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public interface ActionNode {
	Action createAction(ActionEnvironment actionEnvironment);
}
