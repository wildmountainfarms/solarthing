package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface KnownSourceIdentifierFragment<T extends Identifier> extends SourceIdentifierFragment {
	@Override
	KnownIdentifierFragment<T> getIdentifierFragment();
}
