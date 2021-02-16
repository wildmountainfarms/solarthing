package me.retrodaredevil.solarthing.packets.security.crypto;

import com.fasterxml.jackson.core.Base64Variants;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.security.IntegrityPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.security.PublicKey;

import static java.util.Objects.requireNonNull;

@UtilityClass
public final class Decrypt {
	private Decrypt(){ throw new UnsupportedOperationException(); }

	public static String decrypt(Cipher cipher, PublicKeyLookUp publicKeyLookUp, IntegrityPacket packet) throws NotAuthorizedException, DecryptException, InvalidKeyException {
		return decrypt(cipher, publicKeyLookUp, packet.getSender(), packet.getEncryptedData());
	}
	public static String decrypt(Cipher cipher, PublicKeyLookUp publicKeyLookUp, String sender, String base64EncryptedData) throws NotAuthorizedException, DecryptException, InvalidKeyException {
		PublicKey key = publicKeyLookUp.getKey(sender);
		if(key == null){
			throw new NotAuthorizedException(sender + " is not authenticated");
		}
		return decrypt(cipher, key, base64EncryptedData);
	}
	public static String decrypt(Cipher cipher, PublicKey key, String base64EncryptedData) throws InvalidKeyException, DecryptException {
		requireNonNull(cipher);
		requireNonNull(key);
		requireNonNull(base64EncryptedData);
		byte[] encryptedData = Base64Variants.getDefaultVariant().decode(base64EncryptedData);
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
