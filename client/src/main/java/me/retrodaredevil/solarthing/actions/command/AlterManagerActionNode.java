package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.OpenDatabaseCacheEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SolarThingDatabaseEnvironment;

@JsonTypeName("alter_manager")
public class AlterManagerActionNode implements ActionNode {
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		SolarThingDatabaseEnvironment solarThingDatabaseEnvironment = actionEnvironment.getInjectEnvironment().get(SolarThingDatabaseEnvironment.class);
		OpenDatabaseCacheEnvironment openDatabaseCacheEnvironment = actionEnvironment.getInjectEnvironment().get(OpenDatabaseCacheEnvironment.class);

		// This action is designed to be used in the automation program, which will create a new action each iteration. That's why this only runs once
		return Actions.createRunOnce(() -> {
			throw new UnsupportedOperationException("TODO");
		});
	}
}
