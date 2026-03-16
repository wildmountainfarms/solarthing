package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@NullMarked
public class DefaultSupplementaryIdentifier<T extends Identifier> implements KnownSupplementaryIdentifier<T> {
	private final T identifier;
	private final String supplementaryType;

	public DefaultSupplementaryIdentifier(T identifier, String supplementaryType) {
		this.identifier = requireNonNull(identifier);
		this.supplementaryType = requireNonNull(supplementaryType);
	}

	// TODO remove NonNull
	@Override
	public @NonNull T getSupplementaryTo() {
		return identifier;
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getRepresentation() {
		return "SupplementaryIdentifier(identifier=" + identifier + ", supplementaryType=" + supplementaryType + ")";
	}

	@Override
	public boolean equals(@Nullable Object o) {
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
