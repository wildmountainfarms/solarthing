package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;

import javax.validation.constraints.NotNull;

/**
 * Represents something that is "identifiable"
 * <p>
 * {@link #getIdentifier()} is usually used to compare things and usually has information about the packet type or object type.
 * And {@link Identifier} should be unique to a fragment but otherwise may conflict with packets/objects in other fragments
 * <p>
 * {@link #getIdentityInfo()} is used to get different text display values and is usually purely cosmetic.
 */
public interface Identifiable {
	@GraphQLInclude("identifier")
	@NotNull Identifier getIdentifier();

	@GraphQLInclude("identityInfo")
	@NotNull IdentityInfo getIdentityInfo();
}
