package me.retrodaredevil.solarthing.actions.command;

import me.retrodaredevil.solarthing.type.open.OpenSource;
import me.retrodaredevil.action.node.environment.InjectEnvironment;

public interface EnvironmentUpdater {
	void updateInjectEnvironment(OpenSource source, InjectEnvironment.Builder injectEnvironmentBuilder);

	EnvironmentUpdater DO_NOTHING = (dataSource, injectEnvironmentBuilder) -> {};
}
