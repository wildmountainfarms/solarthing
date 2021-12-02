package me.retrodaredevil.action.node.environment;

import me.retrodaredevil.action.node.util.NanoTimeProvider;

public class NanoTimeProviderEnvironment {
	private final NanoTimeProvider nanoTimeProvider;

	public NanoTimeProviderEnvironment(NanoTimeProvider nanoTimeProvider) {
		this.nanoTimeProvider = nanoTimeProvider;
	}

	public NanoTimeProvider getNanoTimeProvider() {
		return nanoTimeProvider;
	}
}
