package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

import java.io.PrintStream;

import static java.util.Objects.requireNonNull;

@JsonTypeName("print")
public class PrintActionNode implements ActionNode {
	private final PrintStream printStream;
	private final String message;

	@JsonCreator
	public PrintActionNode(
			@JsonProperty(value = "message", required = true) String message,
			@JsonProperty("stderr") Boolean stderr
	) {
		this.message = requireNonNull(message);
		this.printStream = Boolean.TRUE.equals(stderr) ? System.err : System.out;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createRunOnce(() -> {
			printStream.println(message);
		});
	}
}
