package me.retrodaredevil.solarthing.packets.identification;

public interface SupplementaryIdentifiable extends Identifiable {
	@Override
	SupplementaryIdentifier getIdentifier();
}
