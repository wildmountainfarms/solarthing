package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

/**
 * This is usually used as a unique key that groups a fragmentId and an {@link Identifier} together.
 */
public interface IdentifierFragment { // TODO make all implementations serializable into JSON
	@Nullable Integer getFragmentId();
	@NotNull Identifier getIdentifier();

	static <T extends Identifier> KnownIdentifierFragment<T> create(Integer fragmentId, T identifier) {
		return new IdentifierFragmentBase<T>(fragmentId, identifier);
	}
}
