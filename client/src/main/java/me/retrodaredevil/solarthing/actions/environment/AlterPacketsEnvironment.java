package me.retrodaredevil.action.node.environment;

import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;

import static java.util.Objects.requireNonNull;

public class AlterPacketsEnvironment {

	private final AlterPacketsProvider alterPacketsProvider;


	public AlterPacketsEnvironment(AlterPacketsProvider alterPacketsProvider) {
		requireNonNull(this.alterPacketsProvider = alterPacketsProvider);
	}

	/**
	 * @return An {@link AlterPacketsProvider} where each {@link StoredAlterPacket#getSourceId()} is the source ID that this automation program is using
	 */
	public AlterPacketsProvider getAlterPacketsProvider() {
		return alterPacketsProvider;
	}
}
