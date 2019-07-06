package me.retrodaredevil.solarthing.packets.security.crypto;

import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class EncryptionTest {
	@Test
	void testEncryptDecrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, EncryptException, InvalidKeyException, DecryptException {
		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
		gen.initialize(1024);
		KeyPair pair = gen.generateKeyPair();
		PrivateKey privateKey = pair.getPrivate();
		PublicKey publicKey = pair.getPublic();
		
		final String toEncrypt = "Hello there how are you?";
		Cipher cipher = Cipher.getInstance("RSA");
		String encrypted = Encrypt.encrypt(cipher, privateKey, toEncrypt);
		System.out.println(encrypted);
		String decrypted = Decrypt.decrypt(cipher, publicKey, encrypted);
		
		assertEquals(toEncrypt, decrypted);
	}
}
