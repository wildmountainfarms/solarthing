package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@NullMarked
public class IdentifierFragmentBase<T extends Identifier> implements KnownIdentifierFragment<T> {
	private final int fragmentId;
	private final T identifier;

	protected IdentifierFragmentBase(int fragmentId, T identifier) {
		this.fragmentId = fragmentId;
		this.identifier = requireNonNull(identifier);
	}

	@Override
	public int getFragmentId() {
		return fragmentId;
	}

	// TODO remove NonNull
	@Override
	public @NonNull T getIdentifier() {
		return identifier;
	}

	@Override
	public String toString() {
		return "IdentifierFragment(" +
				"fragmentId=" + fragmentId +
				", identifier=" + identifier.getRepresentation() +
				')';
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof IdentifierFragment)) return false;
		IdentifierFragment that = (IdentifierFragment) o;
		return matches(that);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fragmentId, identifier);
	}
}
