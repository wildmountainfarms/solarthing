package me.retrodaredevil.solarthing.packets.security.crypto;

import me.retrodaredevil.solarthing.packets.security.IntegrityPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.util.Base64;

import static java.util.Objects.requireNonNull;

public final class Decrypt {
	private Decrypt(){ throw new UnsupportedOperationException(); }
	
	public static String decrypt(Cipher cipher, PublicKeyLookUp publicKeyLookUp, IntegrityPacket packet) throws CryptoException {
		PublicKey key = publicKeyLookUp.getKey(packet.getSender());
		if(key == null){
			throw new CryptoException(packet.getSender() + " is not authenticated");
		}
		return decrypt(cipher, key, packet.getEncryptedData());
	}
	public static String decrypt(Cipher cipher, PublicKey key, String base64EncryptedData) throws CryptoException {
		requireNonNull(cipher);
		requireNonNull(key);
		requireNonNull(base64EncryptedData);
		byte[] encryptedData = Base64.getDecoder().decode(base64EncryptedData);
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			throw new CryptoException("invalid key!", e);
		}
		final byte[] characterByteArray;
		try {
			characterByteArray = cipher.doFinal(encryptedData);
		} catch (IllegalBlockSizeException | BadPaddingException e) { // TODO we may actually have to return null if we receive one of these
			throw new CryptoException(e);
		}
		return new String(characterByteArray);
	}
}
