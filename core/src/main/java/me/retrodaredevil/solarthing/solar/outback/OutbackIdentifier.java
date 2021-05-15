package me.retrodaredevil.solarthing.solar.outback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IntegerIdentifier;
import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.Objects;

@JsonTypeName("outback")
@JsonExplicit
public class OutbackIdentifier implements IntegerIdentifier, OutbackData, Comparable<Identifier> {
	private final int address;

	@JsonCreator
	public OutbackIdentifier(@JsonProperty(value = "address", required = true) int address) {
		this.address = address;
	}

	@JsonProperty("address")
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
