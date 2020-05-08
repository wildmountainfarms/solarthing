package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;
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

}
