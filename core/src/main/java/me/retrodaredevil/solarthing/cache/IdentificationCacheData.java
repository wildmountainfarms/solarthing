package me.retrodaredevil.solarthing.cache;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifier;

public interface IdentificationCacheData {
	@NotNull Identifier getIdentifier();

	/**
	 *
	 * @param other A {@link IdentificationCacheData} from a period after this {@link IdentificationCacheData}'s period. This must be the same type and should have the same identifier.
	 *              This gives undefined behavior if the identifier is not the same.
	 * @return
	 * @throws ClassCastException If {@code other} is not the same type as this
	 */
	IdentificationCacheData combine(IdentificationCacheData other);
}
