package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface NumberedIdentifiable extends Identifiable, Numbered {
	@Override
	NumberedIdentifier getIdentifier();
}
