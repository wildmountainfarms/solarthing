package me.retrodaredevil.solarthing.packets.security.crypto;

import com.fasterxml.jackson.core.Base64Variants;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public final class HashUtil {
	private HashUtil() { throw new UnsupportedOperationException(); }

	private static final MessageDigest MESSAGE_DIGEST;

	static {
		try {
			MESSAGE_DIGEST = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	public static byte[] hash(String string) {
		synchronized (MESSAGE_DIGEST) {
			MESSAGE_DIGEST.update(string.getBytes(StandardCharsets.UTF_8));
			return MESSAGE_DIGEST.digest();
		}
	}
	public static String encodedHash(String string) {
		return Base64Variants.getDefaultVariant().encode(hash(string));
	}
}
