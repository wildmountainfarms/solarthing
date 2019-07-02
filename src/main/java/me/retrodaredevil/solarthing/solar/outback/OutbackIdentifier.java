package me.retrodaredevil.solarthing.solar.outback;

import me.retrodaredevil.solarthing.packets.identification.IntegerIdentifier;

import java.util.Objects;

public class OutbackIdentifier implements IntegerIdentifier {
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
}
