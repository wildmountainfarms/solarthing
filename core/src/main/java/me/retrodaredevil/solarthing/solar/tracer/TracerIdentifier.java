package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.SerializeNameDefinedInBase;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.NumberedIdentifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverIdentifier;

import java.util.Objects;

public final class TracerIdentifier implements NumberedIdentifier, Comparable<Identifier> {
	private final int number;

	private TracerIdentifier(int number) {
		this.number = number;
	}
	public static TracerIdentifier createFromNumber(int number) {
		return new TracerIdentifier(number);
	}

	@SerializeNameDefinedInBase
	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public @NotNull String getRepresentation() {
		if (number != NumberedIdentifier.DEFAULT_NUMBER) {
			return "TracerIdentifier(number=" + number + ")";
		}
		return "TracerIdentifier()";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TracerIdentifier that = (TracerIdentifier) o;
		return number == that.number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(number) + 1; // add one so we don't conflict with RoverIdentifier (We could change how this is done later to something different
	}

	@Override
	public int compareTo(@NotNull Identifier o) {
		if(o instanceof TracerIdentifier){
			return Integer.compare(number, ((TracerIdentifier) o).number);
		}
		if(o instanceof RoverIdentifier){
			return 1;
		}
		if(o instanceof OutbackIdentifier){
			return 1; // renogy devices show up after outback devices
		}
		return -1; // whatever it is should show up after us
	}
}
