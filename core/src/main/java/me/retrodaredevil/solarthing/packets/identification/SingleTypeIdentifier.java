package me.retrodaredevil.solarthing.packets.identification;

import javax.validation.constraints.NotNull;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class SingleTypeIdentifier implements Identifier {
	private final String type;

	public SingleTypeIdentifier(String type) {
		this.type = requireNonNull(type);
	}

	@Override
	public @NotNull String getRepresentation() {
		return "SingleTypeIdentifier(type=" + type + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SingleTypeIdentifier that = (SingleTypeIdentifier) o;
		return type.equals(that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public int compareTo(@NotNull Identifier identifier) {
		if (identifier instanceof SingleTypeIdentifier) {
			return type.compareTo(((SingleTypeIdentifier) identifier).type);
		}
		return 1;
	}

}
