package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

/**
 * This is usually used as a unique key that groups a fragmentId and an {@link Identifier} together.
 */
public interface IdentifierFragment extends IdentifierFragmentMatcher { // TODO make all implementations serializable into JSON
	int getFragmentId();
	@NotNull Identifier getIdentifier();

	@Override
	default boolean matches(IdentifierFragment identifierFragment) {
		return getFragmentId() == identifierFragment.getFragmentId() && getIdentifier().equals(identifierFragment.getIdentifier());
	}

	static <T extends Identifier> KnownIdentifierFragment<T> create(int fragmentId, T identifier) {
		return new IdentifierFragmentBase<>(fragmentId, identifier);
	}
}
