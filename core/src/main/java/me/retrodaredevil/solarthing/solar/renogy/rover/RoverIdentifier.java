package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.SerializeNameDefinedInBase;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.NumberedIdentifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

import java.util.Objects;

/**
 * All {@link RoverIdentifier}s are equal to each other.
 * <p>
 * In the future, if multiple rovers are able to be read at the same time/in the same program, that means that
 * there could be multiple rovers across a single fragment. Right now, this is not the case so, we can assume that
 * all {@link RoverIdentifier}s are equal to each other.
 */
@JsonTypeName("rover")
@JsonExplicit
public final class RoverIdentifier implements NumberedIdentifier, Comparable<Identifier> {

	private final int number;

	private RoverIdentifier(int number) {
		this.number = number;
	}

	public static RoverIdentifier getFromNumber(int number) {
		return new RoverIdentifier(number);
	}
	@JsonCreator
	private static RoverIdentifier deserialize(@JsonProperty("number") Integer number) {
		return getFromNumber(number == null ? 0 : number);
	}

	@SerializeNameDefinedInBase
	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public @NotNull String getRepresentation() {
		if (number != NumberedIdentifier.DEFAULT_NUMBER) {
			return "RoverIdentifier(number=" + number + ")";
		}
		return "RoverIdentifier()";
	}

	@Override
	public String toString() {
		return getRepresentation();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RoverIdentifier that = (RoverIdentifier) o;
		return number == that.number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}


	@Override
	public int compareTo(@NotNull Identifier o) {
		if(o instanceof RoverIdentifier){
			return Integer.compare(number, ((RoverIdentifier) o).number);
		}
		if(o instanceof OutbackIdentifier){
			return 1; // renogy devices show up after outback devices
		}
		return -1; // whatever it is should show up after us
	}
}
