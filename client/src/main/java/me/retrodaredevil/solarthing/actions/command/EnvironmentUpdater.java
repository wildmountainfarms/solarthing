package me.retrodaredevil.solarthing.actions.command;

import me.retrodaredevil.solarthing.DataSource;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;

public interface EnvironmentUpdater {
	void updateInjectEnvironment(DataSource dataSource, InjectEnvironment.Builder injectEnvironmentBuilder);

	EnvironmentUpdater DO_NOTHING = (dataSource, injectEnvironmentBuilder) -> {};
}
