package me.retrodaredevil.solarthing.actions.mate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.type.open.OpenSource;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.MateCommandEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceEnvironment;
import me.retrodaredevil.solarthing.commands.command.SourcedCommand;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import java.util.Queue;

@JsonTypeName("matecommand")
public class MateCommandActionNode implements ActionNode {

	private final MateCommand mateCommand;

	public MateCommandActionNode(@JsonProperty("command") MateCommand mateCommand) {
		this.mateCommand = mateCommand;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		MateCommandEnvironment mateCommandEnvironment = actionEnvironment.getInjectEnvironment().get(MateCommandEnvironment.class);
		SourceEnvironment sourceEnvironment = actionEnvironment.getInjectEnvironment().get(SourceEnvironment.class);
		Queue<? super SourcedCommand<MateCommand>> queue = mateCommandEnvironment.getQueue();
		OpenSource source = sourceEnvironment.getSource();
		return Actions.createRunOnce(() -> queue.add(new SourcedCommand<>(source, mateCommand)));
	}
}
