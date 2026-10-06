package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface KnownSupplementaryIdentifier<T extends Identifier> extends SupplementaryIdentifier {
	@Override
	T getSupplementaryTo();
}
