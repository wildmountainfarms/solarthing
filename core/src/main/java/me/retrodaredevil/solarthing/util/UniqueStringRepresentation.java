package me.retrodaredevil.solarthing.util;

import me.retrodaredevil.solarthing.annotations.NotNull;

/**
 * An interface containing a getter for a unique string that describes this object.
 * <p>
 * Note: If you are implementing {@link #getUniqueString()}, check to see if one of your superclasses or superinterfaces require
 * you to override {@link #equals(Object)}. It is not required by this interface, but is common among subinterfaces that implement this.
 */
public interface UniqueStringRepresentation {
	/*
	If you're looking at this class, you might be wondering why don't I just use toString()? Well, if I just use toString(),
	I don't actually know which of my classes implement it, and I cannot force certain classes to implement toString() for themselves.

	So in some ways, this is just an extra interface which forces implementations to define some method which has the same effect as toString()
	 */

	/**
	 * Note: If
	 * {@link UniqueStringRepresentation#equals(Object) executionReason.equals}({@link UniqueStringRepresentation anotherReason}),
	 * then
	 * {@link UniqueStringRepresentation#getUniqueString() executionReason.getUniqueString()}.{@link UniqueStringRepresentation#equals(Object) equals}({@link UniqueStringRepresentation#getUniqueString() anotherReason.getUniqueString()})
	 * is also true
	 * <p>
	 * NOTE: Implementations of this method can and may be changed in the future, so you should not persist the result of this method anywhere.
	 * @return A string that is not human-readable, but is unique to the contents of this particular {@link UniqueStringRepresentation}
	 */
	@NotNull String getUniqueString();
}
