package me.retrodaredevil.solarthing.solar;

/**
 * @deprecated Use {@link #getIdentifier()} instead. This class may be removed in the future. This is deprecated because
 * different brands may have the same address and a universal unique address is not feasible.
 */
@Deprecated
public interface AddressedSolarPacket extends SolarPacket {
	int getAddress();
}
