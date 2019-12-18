package me.retrodaredevil.solarthing.packets.identification;

public interface SupplementaryIdentifier extends Identifier {
	/**
	 * @return The {@link Identifier} that this is supplementary to
	 */
	Identifier getSupplementaryTo();
}
