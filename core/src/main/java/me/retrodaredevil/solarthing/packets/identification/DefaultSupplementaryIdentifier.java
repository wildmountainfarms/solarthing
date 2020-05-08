package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class DefaultSupplementaryIdentifier<T extends Identifier> implements SupplementaryIdentifier {
	private final T identifier;
	private final String supplementaryType;

	public DefaultSupplementaryIdentifier(T identifier, String supplementaryType) {
		this.identifier = requireNonNull(identifier);
		this.supplementaryType = requireNonNull(supplementaryType);
	}

	@NotNull
	@Override
	public T getSupplementaryTo() {
		return identifier;
	}

	@Override
	public @NotNull String getRepresentation() {
		return "SupplementaryIdentifier(identifier=" + identifier + ", supplementaryType=" + supplementaryType + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultSupplementaryIdentifier<?> that = (DefaultSupplementaryIdentifier<?>) o;
		return identifier.equals(that.identifier) &&
				supplementaryType.equals(that.supplementaryType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier, supplementaryType);
	}

}
