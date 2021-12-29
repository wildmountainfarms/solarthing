package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface NumberedIdentifiable extends Identifiable, Numbered {
	@Override
	@NotNull NumberedIdentifier getIdentifier();
}
