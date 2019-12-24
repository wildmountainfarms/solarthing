package me.retrodaredevil.solarthing.packets.identification;

import org.jetbrains.annotations.NotNull;

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
	public String getRepresentation() {
		return "SupplementaryIdentifier(identifier=" + identifier + ", supplementaryType=" + supplementaryType + ")";
	}
}
