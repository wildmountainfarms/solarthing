package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@NullMarked
public class SourceIdentifierFragmentBase<T extends Identifier> implements KnownSourceIdentifierFragment<T> {
	private final String sourceId;
	private final KnownIdentifierFragment<T> identifierFragment;

	public SourceIdentifierFragmentBase(String sourceId, KnownIdentifierFragment<T> identifierFragment) {
		this.sourceId = requireNonNull(sourceId);
		this.identifierFragment = requireNonNull(identifierFragment);
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public KnownIdentifierFragment<T> getIdentifierFragment() {
		return identifierFragment;
	}

	@Override
	public boolean equals(@Nullable Object o) {
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
