package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;

public interface KnownSourceIdentifierFragment<T extends Identifier> extends SourceIdentifierFragment {
	@Override
	@NonNull KnownIdentifierFragment<T> getIdentifierFragment();
}
