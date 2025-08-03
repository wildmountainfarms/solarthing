package me.retrodaredevil.solarthing.actions.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.PassActionNode;
import me.retrodaredevil.action.node.QueueActionNode;
import me.retrodaredevil.action.node.RaceActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

@JsonTypeName("writetext")
public class WriteTextActionNode implements ActionNode {

	private static final Logger LOGGER = LoggerFactory.getLogger(WriteTextActionNode.class);

	private final Path file;
	private final String text;

	private final ActionNode onFailureAction;
	private final ActionNode finallyAction;

	public WriteTextActionNode(
			@JsonProperty(value = "file", required = true) Path file,
			@JsonProperty(value = "text", required = true) String text,
			@JsonProperty("onfail") ActionNode onFailureAction,
			@JsonProperty("finally") ActionNode finallyAction
	) {
		this.file = requireNonNull(file);
		this.text = requireNonNull(text);
		this.onFailureAction = onFailureAction == null ? PassActionNode.getInstance() : onFailureAction;
		this.finallyAction = finallyAction == null ? PassActionNode.getInstance() : finallyAction;
	}

	private Action createWriteText() {
		return new SimpleAction(false) {
			@Override
			protected void onStart() {
				super.onStart();
				try {
					Files.writeString(file, text, StandardCharsets.UTF_8);
				} catch (IOException e) {
					LOGGER.atError()
							.setMessage("Failed to write text to file")
							.addKeyValue("file", file)
							.setCause(e)
							.log();
					return;
				}
				setDone(true);
			}
		};
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		ActionNode race = new RaceActionNode(Arrays.asList(
				new RaceActionNode.RaceNode(
						_env -> createWriteText(),
						finallyAction
				),
				new RaceActionNode.RaceNode(
						PassActionNode.getInstance(),
						new QueueActionNode(Arrays.asList(onFailureAction, finallyAction))
				)
		));
		return race.createAction(actionEnvironment);
	}
}
