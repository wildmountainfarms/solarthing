package me.retrodaredevil.solarthing.actions.mate;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.MateCommandEnvironment;

import java.util.Queue;

@JsonTypeName("matecommandwait")
public class MateCommandWaitActionNode implements ActionNode {

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		MateCommandEnvironment mateCommandEnvironment = actionEnvironment.getInjectEnvironment().get(MateCommandEnvironment.class);
		Queue<?> queue = mateCommandEnvironment.getQueue();
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				setDone(queue.isEmpty());
			}
		};
	}
}
