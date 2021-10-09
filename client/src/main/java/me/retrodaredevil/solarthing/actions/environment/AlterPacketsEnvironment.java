package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.AlterPacketsProvider;

import static java.util.Objects.requireNonNull;

public class AlterPacketsEnvironment {

	private final AlterPacketsProvider alterPacketsProvider;


	public AlterPacketsEnvironment(AlterPacketsProvider alterPacketsProvider) {
		requireNonNull(this.alterPacketsProvider = alterPacketsProvider);
	}

	public AlterPacketsProvider getAlterPacketsProvider() {
		return alterPacketsProvider;
	}
}
