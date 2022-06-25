package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * This should not be serialized or exposed in a public API. This is used when something inconveniently inherits {@link Identifiable} and
 * you need a {@link SupplementaryIdentifier}
 * @param <T> The type of {@link Identifier} that this is supplementary to
 */
public class UnknownSupplementaryIdentifier<T extends Identifier> implements SupplementaryIdentifier {
	private final T identifier;

	// TODO looking at this on 2022.06.24, why do we need this class when we have DefaultSupplementaryIdentifier

	public UnknownSupplementaryIdentifier(T identifier) {
		this.identifier = requireNonNull(identifier);
	}

	@NotNull
	@Override
	public T getSupplementaryTo() {
		return identifier;
	}

	@Override
	public @NotNull String getRepresentation() {
		return "UnknownSupplementaryIdentifier(identifier=" + identifier + ")";
	}

	// don't override equals or hashCode
}
