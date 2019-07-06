package me.retrodaredevil.solarthing.packets.security;

public interface IntegrityPacket extends SecurityPacket, SenderPacket {
	
	/**
	 * Should be serialized as "encryptedData"
	 * <p>
	 * When unencrypted it looks like this:
	 * [dateMillis in hex],[data]
	 * @return The data. First encrypted with the sender's private key, then encrypted in base64
	 */
	String getEncryptedData();
}
