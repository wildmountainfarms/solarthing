package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IntegerIdentifier extends Identifier {
	int getIntegerIdentifier();
}
