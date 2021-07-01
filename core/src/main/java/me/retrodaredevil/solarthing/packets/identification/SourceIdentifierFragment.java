package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface SourceIdentifierFragment {
	@NotNull String getSourceId();
	@NotNull IdentifierFragment getIdentifierFragment();

	static <T extends Identifier> KnownSourceIdentifierFragment<T> create(String sourceId, KnownIdentifierFragment<T> identifierFragment) {
		return new SourceIdentifierFragmentBase<>(sourceId, identifierFragment);
	}
	static <T extends Identifier> KnownSourceIdentifierFragment<T> create(String sourceId, int fragmentId, T identifier) {
		return create(sourceId, IdentifierFragment.create(fragmentId, identifier));
	}
	static SourceIdentifierFragment create(String sourceId, IdentifierFragment identifierFragment) {
		return create(sourceId, identifierFragment.getFragmentId(), identifierFragment.getIdentifier());
	}
}
