package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * This should not be serialized or exposed in a public API. This is used when something inconveniently inherits {@link Identifiable} and
 * you need a {@link SupplementaryIdentifier}
 * @param <T>
 */
public class UnknownSupplementaryIdentifier<T extends Identifier> implements SupplementaryIdentifier {
	private final T identifier;

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
