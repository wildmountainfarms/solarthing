package me.retrodaredevil.solarthing.actions.command;

import me.retrodaredevil.solarthing.open.OpenSource;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;

public interface EnvironmentUpdater {
	void updateInjectEnvironment(OpenSource source, InjectEnvironment.Builder injectEnvironmentBuilder);

	EnvironmentUpdater DO_NOTHING = (dataSource, injectEnvironmentBuilder) -> {};
}
