package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@NullMarked
public class SingleTypeIdentifier implements Identifier {
	private final String type;

	public SingleTypeIdentifier(String type) {
		this.type = requireNonNull(type);
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getRepresentation() {
		return "SingleTypeIdentifier(type=" + type + ")";
	}

	@Override
	public boolean equals(@Nullable Object o) {
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
