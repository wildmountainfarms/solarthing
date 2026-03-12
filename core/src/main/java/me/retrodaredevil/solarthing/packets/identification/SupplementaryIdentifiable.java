package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SupplementaryIdentifiable extends Identifiable {
	// TODO remove NonNull
	@Override
	@NonNull SupplementaryIdentifier getIdentifier();
}
