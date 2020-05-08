package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface KnownSupplementaryIdentifier<T extends Identifier> extends SupplementaryIdentifier {
	@Override
	@NotNull T getSupplementaryTo();
}
