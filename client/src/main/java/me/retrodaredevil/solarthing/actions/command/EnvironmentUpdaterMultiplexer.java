package me.retrodaredevil.solarthing.actions.command;

import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.reason.ExecutionReason;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EnvironmentUpdaterMultiplexer implements EnvironmentUpdater {
	private final List<EnvironmentUpdater> environmentUpdaterList;

	public EnvironmentUpdaterMultiplexer(Collection<? extends EnvironmentUpdater> environmentUpdaters) {
		this.environmentUpdaterList = List.copyOf(environmentUpdaters);
	}
	public EnvironmentUpdaterMultiplexer(EnvironmentUpdater... environmentUpdaters) {
		this.environmentUpdaterList = Collections.unmodifiableList(Arrays.asList(environmentUpdaters));
	}

	@Override
	public void updateInjectEnvironment(ExecutionReason executionReason, InjectEnvironment.Builder injectEnvironmentBuilder) {
		for (EnvironmentUpdater environmentUpdater : environmentUpdaterList) {
			environmentUpdater.updateInjectEnvironment(executionReason, injectEnvironmentBuilder);
		}
	}
}
