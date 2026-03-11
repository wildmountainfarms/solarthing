package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;

public interface NumberedIdentifiable extends Identifiable, Numbered {
	@Override
	@NonNull NumberedIdentifier getIdentifier();
}
