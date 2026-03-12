package me.retrodaredevil.solarthing.misc.error;

import me.retrodaredevil.solarthing.packets.identification.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ExceptionErrorIdentifier implements Identifier {
	private final String exceptionCatchLocationIdentifier;
	private final String exceptionInstanceIdentifier;

	public ExceptionErrorIdentifier(String exceptionCatchLocationIdentifier, String exceptionInstanceIdentifier) {
		this.exceptionCatchLocationIdentifier = requireNonNull(exceptionCatchLocationIdentifier);
		this.exceptionInstanceIdentifier = requireNonNull(exceptionInstanceIdentifier);
	}

	public String getExceptionCatchLocationIdentifier() {
		return exceptionCatchLocationIdentifier;
	}

	public String getExceptionInstanceIdentifier() {
		return exceptionInstanceIdentifier;
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getRepresentation() {
		return "ExceptionErrorIdentifier(exceptionCatchLocationIdentifier='" + exceptionCatchLocationIdentifier + "', exceptionInstanceIdentifier='" + exceptionInstanceIdentifier + "')";
	}
	@Override
	public String toString() {
		return getRepresentation();
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ExceptionErrorIdentifier that = (ExceptionErrorIdentifier) o;
		return exceptionCatchLocationIdentifier.equals(that.exceptionCatchLocationIdentifier) &&
				exceptionInstanceIdentifier.equals(that.exceptionInstanceIdentifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(exceptionCatchLocationIdentifier, exceptionInstanceIdentifier);
	}
}
