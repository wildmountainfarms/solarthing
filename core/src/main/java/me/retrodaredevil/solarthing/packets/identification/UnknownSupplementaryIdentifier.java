package me.retrodaredevil.solarthing.packets.identification;

import javax.validation.constraints.NotNull;
import java.util.Objects;

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

	@Override
	public int compareTo(@NotNull Identifier identifier) {
		return 1;
	}
	// don't override equals or hashCode
}
