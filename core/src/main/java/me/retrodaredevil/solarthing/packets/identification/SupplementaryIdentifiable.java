package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface SupplementaryIdentifiable extends Identifiable {
	@Override
	@NotNull SupplementaryIdentifier getIdentifier();
}
