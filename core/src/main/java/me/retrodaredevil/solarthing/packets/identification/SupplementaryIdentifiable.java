package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SupplementaryIdentifiable extends Identifiable {
	@Override
	SupplementaryIdentifier getIdentifier();
}
