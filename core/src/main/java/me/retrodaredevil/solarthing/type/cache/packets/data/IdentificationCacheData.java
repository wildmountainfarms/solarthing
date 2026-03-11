package me.retrodaredevil.solarthing.type.cache.packets.data;

import me.retrodaredevil.solarthing.packets.identification.Identifier;
import org.jspecify.annotations.NonNull;

/**
 * Represents data associated with an identifier and a fragment ID. The fragment ID tracked elsewhere, usually in a level above this object.
 */
public interface IdentificationCacheData {
	@NonNull Identifier getIdentifier();

	/**
	 *
	 * @param other A {@link IdentificationCacheData} from a period after this {@link IdentificationCacheData}'s period. This must be the same type and should have the same identifier.
	 *              This gives undefined behavior if the identifier is not the same.
	 * @return The combined result
	 * @throws ClassCastException If {@code other} is not the same type as this
	 */
	IdentificationCacheData combine(IdentificationCacheData other);
}
