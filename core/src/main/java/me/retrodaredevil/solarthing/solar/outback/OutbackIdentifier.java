package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IntegerIdentifier;
import javax.validation.constraints.NotNull;

import java.util.Objects;

public class OutbackIdentifier implements IntegerIdentifier, OutbackData, Comparable<Identifier> {
	private final int address;

	public OutbackIdentifier(int address) {
		this.address = address;
	}

	@Override
	public int getAddress(){
		return address;
	}
	@Override
	public int getIntegerIdentifier() {
		return address;
	}

	@Override
	public String toString() {
		return getRepresentation();
	}

	@Override
	public @NotNull String getRepresentation() {
		return "OutbackIdentifier(address=" + address + ")";
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
	public int compareTo(@NotNull Identifier o) {
		if(o instanceof OutbackIdentifier){
			return address - ((OutbackIdentifier) o).address;
		}
		return -1; // whatever is is should show up after us no matter what
	}
}
