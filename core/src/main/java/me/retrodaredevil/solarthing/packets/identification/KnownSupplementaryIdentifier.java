package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;

public interface KnownSupplementaryIdentifier<T extends Identifier> extends SupplementaryIdentifier {
	@Override
	@NonNull T getSupplementaryTo();
}
