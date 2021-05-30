package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface NumberedIdentifiable extends Identifiable {
	@Override
	@NotNull NumberedIdentifier getIdentifier();

	int getNumber();
}
