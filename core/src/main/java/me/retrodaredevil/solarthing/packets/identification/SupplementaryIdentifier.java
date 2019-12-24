package me.retrodaredevil.solarthing.packets.identification;

import org.jetbrains.annotations.NotNull;

public interface SupplementaryIdentifier extends Identifier {
	/**
	 * This is not null
	 * @return The {@link Identifier} that this is supplementary to
	 */
	@NotNull
	Identifier getSupplementaryTo();
}
