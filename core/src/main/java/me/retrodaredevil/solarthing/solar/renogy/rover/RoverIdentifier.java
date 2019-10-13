package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IntegerIdentifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

import java.util.Objects;

public final class RoverIdentifier implements IntegerIdentifier, Comparable<Identifier> {
	private final int serialNumber;

	@Override
	public String toString() {
		return getRepresentation();
	}

	@Override
	public String getRepresentation() {
		return "RoverIdentifier(serialNumber=" + serialNumber + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RoverIdentifier that = (RoverIdentifier) o;
		return serialNumber == that.serialNumber;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(serialNumber);
	}
	
	public RoverIdentifier(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	public int getProductSerialNumber(){
		return serialNumber;
	}
	@Override
	public int getIntegerIdentifier() {
		return serialNumber;
	}
	
	@Override
	public int compareTo(Identifier o) {
		if(o instanceof RoverIdentifier){
			return serialNumber - ((RoverIdentifier) o).serialNumber;
		}
		if(o instanceof OutbackIdentifier){
			return 1; // renogy devices show up after outback devices
		}
		return -1; // whatever it is should show up after us
	}
}
