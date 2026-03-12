package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface NumberedIdentifiable extends Identifiable, Numbered {
	// TODO remove NonNull
	@Override
	@NonNull NumberedIdentifier getIdentifier();
}
