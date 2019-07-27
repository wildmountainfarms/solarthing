package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IntegerIdentifier;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverIdentifier;

import java.util.Objects;

public class OutbackIdentifier implements IntegerIdentifier, Comparable<Identifier> {
	private final int address;
	
	public OutbackIdentifier(int address) {
		this.address = address;
	}
	
	@Override
	public int getIntegerIdentifier() {
		return address;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OutbackIdentifier that = (OutbackIdentifier) o;
		return address == that.address;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(address);
	}
	
	@Override
	public int compareTo(Identifier o) {
		if(o instanceof OutbackIdentifier){
			return address - ((OutbackIdentifier) o).address;
		}
		if(o instanceof RoverIdentifier){
			return -1; // Outback devices should show up before Renogy devices
		}
		return 0;
	}
}
