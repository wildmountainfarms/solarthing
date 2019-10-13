package me.retrodaredevil.solarthing.packets.security.crypto;

import me.retrodaredevil.solarthing.packets.security.IntegrityPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.security.PublicKey;
import java.util.Base64;

import static java.util.Objects.requireNonNull;

public final class Decrypt {
	private Decrypt(){ throw new UnsupportedOperationException(); }
	
	public static String decrypt(Cipher cipher, PublicKeyLookUp publicKeyLookUp, IntegrityPacket packet) throws NotAuthorizedException, DecryptException, InvalidKeyException {
		PublicKey key = publicKeyLookUp.getKey(packet.getSender());
		if(key == null){
			throw new NotAuthorizedException(packet.getSender() + " is not authenticated");
		}
		return decrypt(cipher, key, packet.getEncryptedData());
	}
	public static String decrypt(Cipher cipher, PublicKey key, String base64EncryptedData) throws InvalidKeyException, DecryptException {
		requireNonNull(cipher);
		requireNonNull(key);
		requireNonNull(base64EncryptedData);
		byte[] encryptedData = Base64.getDecoder().decode(base64EncryptedData);
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException("invalid key!", e);
		}
		final byte[] characterByteArray;
		try {
			characterByteArray = cipher.doFinal(encryptedData);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new DecryptException(e);
		}
		return new String(characterByteArray);
	}
}
