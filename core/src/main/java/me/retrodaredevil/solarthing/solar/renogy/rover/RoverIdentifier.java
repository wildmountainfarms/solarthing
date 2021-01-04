package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

/**
 * All {@link RoverIdentifier}s are equal to each other.
 * <p>
 * In the future, if multiple rovers are able to be read at the same time/in the same program, that means that
 * there could be multiple rovers across a single fragment. Right now, this is not the case so, we can assume that
 * all {@link RoverIdentifier}s are equal to each other.
 */
public final class RoverIdentifier implements Identifier, Comparable<Identifier> {
	private static final RoverIdentifier DEFAULT_IDENTIFIER = new RoverIdentifier();

	private RoverIdentifier() {
	}

	/**
	 * @return A {@link RoverIdentifier} that is equal to all other rover identifiers.
	 */
	public static RoverIdentifier getDefaultIdentifier() {
		return DEFAULT_IDENTIFIER;
	}

	@Override
	public @NotNull String getRepresentation() {
		return "RoverIdentifier()";
	}

	@Override
	public String toString() {
		return getRepresentation();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		return o != null && getClass() == o.getClass();
	}

	@Override
	public int hashCode() {
		return -1908729263;
	}


	@Override
	public int compareTo(@NotNull Identifier o) {
		if(o instanceof RoverIdentifier){
			return 0;
		}
		if(o instanceof OutbackIdentifier){
			return 1; // renogy devices show up after outback devices
		}
		return -1; // whatever it is should show up after us
	}
}
