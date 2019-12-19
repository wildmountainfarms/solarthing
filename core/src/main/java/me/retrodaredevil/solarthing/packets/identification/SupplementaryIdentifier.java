package me.retrodaredevil.solarthing.packets.identification;

public interface SupplementaryIdentifier extends Identifier {
	/**
	 * This is not null
	 * @return The {@link Identifier} that this is supplementary to
	 */
	Identifier getSupplementaryTo();
}
