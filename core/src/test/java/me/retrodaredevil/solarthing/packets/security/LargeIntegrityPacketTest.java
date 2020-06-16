package me.retrodaredevil.solarthing.packets.security;

import com.fasterxml.jackson.core.Base64Variants;
import me.retrodaredevil.solarthing.packets.security.crypto.*;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class LargeIntegrityPacketTest {

	@Test
	void test() throws NoSuchPaddingException, NoSuchAlgorithmException, EncryptException, InvalidKeyException, DecryptException {
		String payload = "{ \"value\": 42 }";
		String data = Long.toHexString(System.currentTimeMillis()) + "," + HashUtil.encodedHash(payload);
		KeyPair keyPair = KeyUtil.generateKeyPair();
		Cipher cipher = Cipher.getInstance(KeyUtil.CIPHER_TRANSFORMATION);
		String encryptedHash = Encrypt.encrypt(cipher, keyPair.getPrivate(), data);

		LargeIntegrityPacket packet = new ImmutableLargeIntegrityPacket("josh", encryptedHash, payload);
		String hashString = Decrypt.decrypt(cipher, keyPair.getPublic(), packet.getEncryptedHash());
		assertEquals(data, hashString);
		assertArrayEquals(Base64Variants.getDefaultVariant().decode(hashString.split(",", 2)[1]), HashUtil.hash(packet.getPayload()));
	}
}
