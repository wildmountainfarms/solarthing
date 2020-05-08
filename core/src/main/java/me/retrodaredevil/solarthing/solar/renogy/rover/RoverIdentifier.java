package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

import java.util.Objects;

/**
 * Currently, all {@link RoverIdentifier}s are equal to each other, no matter what their serial number is. Eventually
 * the serial number will not be used any more.
 * <p>
 * In the future, if multiple rovers are able to be read at the same time/in the same program, that means that
 * there could be multiple rovers across a single fragment. Right now, this is not the case so, we can assume that
 * all {@link RoverIdentifier}s are equal to each other.
 */
public final class RoverIdentifier implements Identifier, Comparable<Identifier> {
	private static final RoverIdentifier DEFAULT_IDENTIFIER = RoverIdentifier.createFromSerialNumber(0);

	private final int serialNumber;
	@Deprecated
	public RoverIdentifier(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * This will eventually be deprecated in the future, but is still in use to make sure {@link #getRepresentation()} returns consistently
	 * for a while into the future.
	 *
	 * @param serialNumber The serial number of the Rover
	 * @return The {@link RoverIdentifier} with the specific serial number
	 */
	public static RoverIdentifier createFromSerialNumber(int serialNumber) {
		return new RoverIdentifier(serialNumber);
	}

	/**
	 * @return A {@link RoverIdentifier} that is equal to all other rover identifiers.
	 */
	public static RoverIdentifier getDefaultIdentifier() {
		return DEFAULT_IDENTIFIER;
	}

	@Deprecated
	public int getProductSerialNumber(){
		return serialNumber;
	}

	@Override
	public @NotNull String getRepresentation() {
		return "RoverIdentifier(serialNumber=" + serialNumber + ")";
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
		return Objects.hash(serialNumber);
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
