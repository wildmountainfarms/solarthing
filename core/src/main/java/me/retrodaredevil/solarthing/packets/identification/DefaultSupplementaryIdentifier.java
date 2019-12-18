package me.retrodaredevil.solarthing.packets.identification;

public class DefaultSupplementaryIdentifier<T extends Identifier> implements SupplementaryIdentifier {
	private final T identifier;
	private final String supplementaryType;

	public DefaultSupplementaryIdentifier(T identifier, String supplementaryType) {
		this.identifier = identifier;
		this.supplementaryType = supplementaryType;
	}

	@Override
	public T getSupplementaryTo() {
		return identifier;
	}

	@Override
	public String getRepresentation() {
		return "SupplementaryIdentifier(identifier=" + identifier + ", supplementaryType=" + supplementaryType + ")";
	}
}
