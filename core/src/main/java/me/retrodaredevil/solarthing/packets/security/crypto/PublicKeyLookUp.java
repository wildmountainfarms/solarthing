package me.retrodaredevil.solarthing.packets.security.crypto;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.security.PublicKey;

@NullMarked
public interface PublicKeyLookUp {
	/**
	 * @param sender The name or id of the sender
	 * @return The public key, if it exists, or null, if it does not exist
	 */
	@Nullable PublicKey getKey(String sender);
}
