package me.retrodaredevil.solarthing.misc.error;

import me.retrodaredevil.solarthing.packets.identification.Identifier;

import me.retrodaredevil.solarthing.annotations.NotNull;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class ExceptionErrorIdentifier implements Identifier {
	private final String exceptionCatchLocationIdentifier;
	private final String exceptionInstanceIdentifier;

	public ExceptionErrorIdentifier(String exceptionCatchLocationIdentifier, String exceptionInstanceIdentifier) {
		requireNonNull(this.exceptionCatchLocationIdentifier = exceptionCatchLocationIdentifier);
		requireNonNull(this.exceptionInstanceIdentifier = exceptionInstanceIdentifier);
	}

	public String getExceptionCatchLocationIdentifier() {
		return exceptionCatchLocationIdentifier;
	}

	public String getExceptionInstanceIdentifier() {
		return exceptionInstanceIdentifier;
	}

	@Override
	public @NotNull String getRepresentation() {
		return "ExceptionIdentifier(exceptionCatchLocationIdentifier='" + exceptionCatchLocationIdentifier + "', exceptionInstanceIdentifier='" + exceptionInstanceIdentifier + "')";
	}
	@Override
	public String toString() {
		return getRepresentation();
	}

	@Override
	public boolean equals(Object o) {
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

	@Deprecated
	@Override
	public int compareTo(@NotNull Identifier identifier) {
		if (identifier instanceof ExceptionErrorIdentifier) {
			int a = exceptionCatchLocationIdentifier.compareTo(((ExceptionErrorIdentifier) identifier).exceptionCatchLocationIdentifier);
			if (a != 0) {
				return a;
			}
			return exceptionInstanceIdentifier.compareTo(((ExceptionErrorIdentifier) identifier).exceptionInstanceIdentifier);
		}
		return -1;
	}
}
