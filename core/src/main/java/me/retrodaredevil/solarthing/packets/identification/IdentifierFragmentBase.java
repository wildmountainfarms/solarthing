package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class IdentifierFragmentBase<T extends Identifier> implements KnownIdentifierFragment<T> {
	private final int fragmentId;
	private final T identifier;

	protected IdentifierFragmentBase(int fragmentId, T identifier) {
		this.fragmentId = fragmentId;
		requireNonNull(this.identifier = identifier);
	}
	public int getFragmentId() {
		return fragmentId;
	}
	public @NotNull T getIdentifier() {
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
	public boolean equals(Object o) {
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
