package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class IdentifierFragmentBase<T extends Identifier> implements KnownIdentifierFragment<T> {
	private final Integer fragmentId;
	private final T identifier;

	protected IdentifierFragmentBase(Integer fragmentId, T identifier) {
		this.fragmentId = fragmentId;
		requireNonNull(this.identifier = identifier);
	}
	public @Nullable Integer getFragmentId() {
		return fragmentId;
	}
	public @NotNull T getIdentifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IdentifierFragment that = (IdentifierFragment) o;
		return Objects.equals(fragmentId, that.getFragmentId()) &&
				identifier.equals(that.getIdentifier());
	}

	@Override
	public int hashCode() {
		return Objects.hash(fragmentId, identifier);
	}
}
