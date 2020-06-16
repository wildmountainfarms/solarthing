package me.retrodaredevil.solarthing.packets.identification;

public interface IdentifierFragmentMatcher {
	boolean matches(IdentifierFragment identifierFragment);

	IdentifierFragmentMatcher NO_MATCH = identifierFragment -> false;
}
