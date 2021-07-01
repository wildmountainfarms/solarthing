package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class SourceIdentifierFragmentBase<T extends Identifier> implements KnownSourceIdentifierFragment<T> {
	private final String sourceId;
	private final KnownIdentifierFragment<T> identifierFragment;

	public SourceIdentifierFragmentBase(String sourceId, KnownIdentifierFragment<T> identifierFragment) {
		requireNonNull(this.sourceId = sourceId);
		requireNonNull(this.identifierFragment = identifierFragment);
	}

	@Override
	public @NotNull String getSourceId() {
		return sourceId;
	}

	@Override
	public @NotNull KnownIdentifierFragment<T> getIdentifierFragment() {
		return identifierFragment;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SourceIdentifierFragment)) return false;
		SourceIdentifierFragment that = (SourceIdentifierFragment) o;
		return sourceId.equals(that.getSourceId()) && identifierFragment.equals(that.getIdentifierFragment());
	}

	@Override
	public int hashCode() {
		return Objects.hash(sourceId, identifierFragment);
	}

	@Override
	public String toString() {
		return "SourceIdentifierFragment{" +
				"sourceId='" + sourceId + '\'' +
				", identifierFragment=" + identifierFragment +
				'}';
	}
}
