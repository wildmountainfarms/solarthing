package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface KnownIdentifierFragment <T extends Identifier> extends IdentifierFragment {
	@Override
	@NotNull T getIdentifier();
}
