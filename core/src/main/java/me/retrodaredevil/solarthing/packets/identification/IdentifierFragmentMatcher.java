package me.retrodaredevil.solarthing.packets.identification;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IdentifierFragmentMatcher {
	boolean matches(IdentifierFragment identifierFragment);

	IdentifierFragmentMatcher NO_MATCH = identifierFragment -> false;
}
