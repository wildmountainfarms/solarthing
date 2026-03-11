package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.scope.ScopeActionNode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(builder = DeclarationActionNode.Builder.class)
@JsonTypeName("declaration")
@NullMarked
public class DeclarationActionNode implements ActionNode {
	private final ActionNode mainDeclaration;
	private final Map<String, ActionNode> declarations;
	@JsonCreator
	public DeclarationActionNode(ActionNode mainDeclaration, Map<String, ActionNode> declarations) {
		this.mainDeclaration = requireNonNull(mainDeclaration);
		this.declarations = requireNonNull(declarations);
	}
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		List<ActionNode> actionNodeList = new ArrayList<>();
		declarations.entrySet().stream().map(entry -> new DefineActionActionNode(entry.getKey(), entry.getValue())).forEach(actionNodeList::add);
		actionNodeList.add(mainDeclaration);
		return new ScopeActionNode(
				new QueueActionNode(actionNodeList)
		).createAction(actionEnvironment);
	}
	static class Builder {
		// nullable for initialization purposes
		@JsonProperty(value = "main", required = true)
		private @Nullable ActionNode mainDeclaration;
		private final Map<String, ActionNode> declarations = new HashMap<>();
		@JsonAnySetter
		public void setActionNode(String name, ActionNode actionNode) {
			declarations.put(name, actionNode);
		}

		DeclarationActionNode build() {
			return new DeclarationActionNode(
					requireNonNull(mainDeclaration, "main cannot be null"),
					declarations
			);
		}
	}
}
