package me.retrodaredevil.solarthing.actions.command;

import me.retrodaredevil.solarthing.type.open.OpenSource;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;

import java.util.*;

public class EnvironmentUpdaterMultiplexer implements EnvironmentUpdater {
	private final List<EnvironmentUpdater> environmentUpdaterList;

	public EnvironmentUpdaterMultiplexer(Collection<? extends EnvironmentUpdater> environmentUpdaters) {
		this.environmentUpdaterList = Collections.unmodifiableList(new ArrayList<>(environmentUpdaters));
	}
	public EnvironmentUpdaterMultiplexer(EnvironmentUpdater... environmentUpdaters) {
		this.environmentUpdaterList = Collections.unmodifiableList(Arrays.asList(environmentUpdaters));
	}

	@Override
	public void updateInjectEnvironment(OpenSource source, InjectEnvironment.Builder injectEnvironmentBuilder) {
		for (EnvironmentUpdater environmentUpdater : environmentUpdaterList) {
			environmentUpdater.updateInjectEnvironment(source, injectEnvironmentBuilder);
		}
	}
}
