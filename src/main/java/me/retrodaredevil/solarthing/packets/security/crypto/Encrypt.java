package me.retrodaredevil.solarthing.packets.security.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.util.Base64;

import static java.util.Objects.requireNonNull;

public final class Encrypt {
	private Encrypt(){ throw new UnsupportedOperationException(); }
	
	public static String encrypt(Cipher cipher, PrivateKey key, String dataToEncrypt) throws InvalidKeyException, EncryptException {
		requireNonNull(cipher);
		requireNonNull(key);
		requireNonNull(dataToEncrypt);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(e);
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output);
		writer.write(dataToEncrypt);
		writer.flush();
		final byte[] encryptedData;
		try {
			encryptedData = cipher.doFinal(output.toByteArray());
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new EncryptException("Exception while encrypting! This should not happen!", e);
		}
		return Base64.getEncoder().encodeToString(encryptedData);
	}
}
