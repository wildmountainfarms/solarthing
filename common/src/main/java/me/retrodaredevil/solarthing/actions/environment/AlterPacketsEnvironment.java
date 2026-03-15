package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class AlterPacketsEnvironment {

	private final AlterPacketsProvider alterPacketsProvider;


	public AlterPacketsEnvironment(AlterPacketsProvider alterPacketsProvider) {
		this.alterPacketsProvider = requireNonNull(alterPacketsProvider);
	}

	/**
	 * @return An {@link AlterPacketsProvider} where each {@link StoredAlterPacket#getSourceId()} is the source ID that this automation program is using
	 */
	public AlterPacketsProvider getAlterPacketsProvider() {
		return alterPacketsProvider;
	}
}
