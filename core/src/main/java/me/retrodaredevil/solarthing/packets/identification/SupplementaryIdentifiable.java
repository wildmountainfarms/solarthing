package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;

public interface SupplementaryIdentifiable extends Identifiable {
	@Override
	@NonNull SupplementaryIdentifier getIdentifier();
}
