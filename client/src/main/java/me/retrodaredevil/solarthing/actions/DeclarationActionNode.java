package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(builder = DeclarationActionNode.Builder.class)
@JsonTypeName("declaration")
public class DeclarationActionNode implements ActionNode {
	private final ActionNode mainDeclaration;
	private final Map<String, ActionNode> declarations;
	@JsonCreator
	public DeclarationActionNode(ActionNode mainDeclaration, Map<String, ActionNode> declarations) {
		requireNonNull(this.mainDeclaration = mainDeclaration);
		requireNonNull(this.declarations = declarations);
	}
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		Action mainAction = mainDeclaration.createAction(actionEnvironment);
		return new Actions.ActionQueueBuilder(
				Actions.createRunOnce(() -> {
					for(Map.Entry<String, ActionNode> entry : declarations.entrySet()) {
						actionEnvironment.getLocalEnvironment().setDeclaredAction(entry.getKey(), entry.getValue());
					}
				}),
				mainAction
		).immediatelyDoNextWhenDone(true).build();
	}
	static class Builder {
		@JsonProperty(value = "main", required = true)
		private ActionNode mainDeclaration;
		private final Map<String, ActionNode> declarations = new HashMap<>();
		@JsonAnySetter
		public void setActionNode(String name, ActionNode actionNode) {
			declarations.put(name, actionNode);
		}

		DeclarationActionNode build() {
			return new DeclarationActionNode(mainDeclaration, declarations);
		}
	}
}
