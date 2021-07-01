package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface KnownSourceIdentifierFragment<T extends Identifier> extends SourceIdentifierFragment {
	@Override
	@NotNull KnownIdentifierFragment<T> getIdentifierFragment();
}
