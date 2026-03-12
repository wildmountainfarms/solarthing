package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface KnownSupplementaryIdentifier<T extends Identifier> extends SupplementaryIdentifier {
	// TODO remove NonNull
	@Override
	@NonNull T getSupplementaryTo();
}
