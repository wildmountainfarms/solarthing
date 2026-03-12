package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

/**
 * This is usually used as a unique key that groups a fragmentId and an {@link Identifier} together.
 */
@NullMarked
public interface IdentifierFragment extends IdentifierFragmentMatcher { // It would be nice to make these serializable to JSON, but nah. Probably not necessary.
	int getFragmentId();
	@NonNull Identifier getIdentifier();

	@Override
	default boolean matches(IdentifierFragment identifierFragment) {
		return getFragmentId() == identifierFragment.getFragmentId() && getIdentifier().equals(identifierFragment.getIdentifier());
	}

	static <T extends Identifier> KnownIdentifierFragment<T> create(int fragmentId, T identifier) {
		return new IdentifierFragmentBase<>(fragmentId, identifier);
	}
}
