package me.retrodaredevil.solarthing.packets.security.crypto;

import java.security.PublicKey;

public interface PublicKeyLookUp {
	/**
	 * @param sender The name or id of the sender
	 * @return The public key, if it exists, or null, if it does not exist
	 */
	PublicKey getKey(String sender);
}
