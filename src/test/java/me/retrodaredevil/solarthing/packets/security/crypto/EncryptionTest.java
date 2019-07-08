package me.retrodaredevil.solarthing.packets.security.crypto;

import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class EncryptionTest {
	@Test
	void testEncryptDecrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, EncryptException, InvalidKeyException, DecryptException {
		KeyPair pair = KeyUtil.generateKeyPair();
		PrivateKey privateKey = pair.getPrivate();
		PublicKey publicKey = pair.getPublic();
		
		final String toEncrypt = "Hello there how are you?";
		Cipher cipher = Cipher.getInstance("RSA");
		String encrypted = Encrypt.encrypt(cipher, privateKey, toEncrypt);
		String decrypted = Decrypt.decrypt(cipher, publicKey, encrypted);
		
		assertEquals(toEncrypt, decrypted);
	}
	@Test
	void testEncryptDecryptFail() throws NoSuchAlgorithmException, NoSuchPaddingException, EncryptException, InvalidKeyException {
		PrivateKey privateKey = KeyUtil.generateKeyPair().getPrivate();
		PublicKey publicKey = KeyUtil.generateKeyPair().getPublic();
		
		final String toEncrypt = "Hello there how are you?";
		Cipher cipher = Cipher.getInstance("RSA");
		String encrypted = Encrypt.encrypt(cipher, privateKey, toEncrypt);
		
		assertThrows(DecryptException.class, () -> Decrypt.decrypt(cipher, publicKey, encrypted));
	}
}
