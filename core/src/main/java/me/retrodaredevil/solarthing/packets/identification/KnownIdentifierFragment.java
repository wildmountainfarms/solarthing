package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;

public interface KnownIdentifierFragment <T extends Identifier> extends IdentifierFragment {
	@Override
	@NonNull T getIdentifier();
}
