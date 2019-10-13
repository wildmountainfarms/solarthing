package me.retrodaredevil.solarthing.packets.security;

import java.security.PublicKey;

public interface AuthNewSenderPacket extends SecurityPacket, SenderPacket {
	/**
	 * Should be serialized as "publicKey"
	 *
	 * @return The base64 encoded public key
	 */
	String getPublicKey();
	
	/**
	 * @return The public key that {@link #getPublicKey()} represents
	 */
	PublicKey getPublicKeyObject();
}
