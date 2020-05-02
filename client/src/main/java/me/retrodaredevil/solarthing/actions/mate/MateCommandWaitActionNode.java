package me.retrodaredevil.solarthing.actions.mate;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.MateCommandEnvironment;

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
