package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

/**
 * Represents something that is "identifiable"
 * <p>
 * {@link #getIdentifier()} is usually used to compare things and usually has information about the packet type or object type.
 * And {@link Identifier} should be unique to a fragment but otherwise may conflict with packets/objects in other fragments
 * <p>
 * {@link #getIdentityInfo()} is used to get different text display values and is usually purely cosmetic.
 */
@NullMarked
public interface Identifiable {
	// TODO remove NonNull
	@GraphQLInclude("identifier")
	@NonNull Identifier getIdentifier();

	// TODO remove NonNull
	@GraphQLInclude("identityInfo")
	@NonNull IdentityInfo getIdentityInfo();
}
