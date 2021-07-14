package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * "IdentifierRepresentationFragment" - represents an identifier's String representation and a fragment
 */
public class IdentifierRepFragment implements IdentifierFragmentMatcher {
	private final int fragmentId;
	private final String identifierRepresentation;

	@JsonCreator
	public IdentifierRepFragment(
			@JsonProperty("fragment") int fragmentId,
			@JsonProperty("identifier") @NotNull String identifierRepresentation) {
		this.fragmentId = fragmentId;
		requireNonNull(this.identifierRepresentation = identifierRepresentation);
	}

	@Override
	public boolean matches(IdentifierFragment identifierFragment) {
		return fragmentId == identifierFragment.getFragmentId()
				&& identifierRepresentation.equals(identifierFragment.getIdentifier().getRepresentation());
	}

	public int getFragmentId() {
		return fragmentId;
	}

	public @NotNull String getIdentifierRepresentation() {
		return identifierRepresentation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IdentifierRepFragment that = (IdentifierRepFragment) o;
		return getFragmentId() == that.getFragmentId() && getIdentifierRepresentation().equals(that.getIdentifierRepresentation());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getIdentifierRepresentation(), getFragmentId());
	}
}
