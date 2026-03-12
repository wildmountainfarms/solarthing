package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface KnownIdentifierFragment <T extends Identifier> extends IdentifierFragment {
	// TODO remove NonNull
	@Override
	@NonNull T getIdentifier();
}
