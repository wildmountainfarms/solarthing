package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.solarthing.open.OpenSource;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SolarThingDatabaseEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceEnvironment;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;

@JsonTypeName("feedback")
public class ExecutingCommandFeedbackActionNode implements ActionNode {
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		SourceEnvironment sourceEnvironment = actionEnvironment.getInjectEnvironment().get(SourceEnvironment.class);
		SolarThingDatabaseEnvironment solarThingDatabaseEnvironment = actionEnvironment.getInjectEnvironment().get(SolarThingDatabaseEnvironment.class);
		OpenSource source = sourceEnvironment.getSource();
		SolarThingDatabase database = solarThingDatabaseEnvironment.getSolarThingDatabase();

		throw new UnsupportedOperationException("Not implemented yet.");
	}
}
