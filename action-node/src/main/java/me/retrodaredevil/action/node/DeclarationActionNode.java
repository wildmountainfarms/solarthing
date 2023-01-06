package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.scope.ScopeActionNode;

import java.util.Arrays;
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
		return new ScopeActionNode(
				new QueueActionNode(Arrays.asList(
						innerActionEnvironment -> Actions.createRunOnce(() -> {
							for(Map.Entry<String, ActionNode> entry : declarations.entrySet()) {
								innerActionEnvironment.getVariableEnvironment().setDeclaredAction(entry.getKey(), entry.getValue());
							}
						}),
						mainDeclaration
				))
		).createAction(actionEnvironment);
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
