package me.retrodaredevil.solarthing.packets.security.crypto;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class KeyUtil {
	private KeyUtil() { throw new UnsupportedOperationException(); }
	
	private static final KeyPairGenerator KEY_PAIR_GENERATOR;
	private static final KeyFactory KEY_FACTORY;
	
	static {
		try {
			KEY_PAIR_GENERATOR = KeyPairGenerator.getInstance("RSA");
			KEY_FACTORY = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("RSA algorithm doesn't exist???", e);
		}
		KEY_PAIR_GENERATOR.initialize(1024);
	}
	
	public static KeyPair generateKeyPair(){
		return KEY_PAIR_GENERATOR.generateKeyPair();
	}
	public static String encodePublicKey(PublicKey key){
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	/**
	 *
	 * @param encodedKey The base64 data where when decoded, represents the public key
	 * @return The {@link PublicKey} represented by {@code encodedKey}
	 * @throws InvalidKeyException Thrown if the {@code encodedKey} is not valid
	 */
	public static PublicKey decodePublicKey(String encodedKey) throws InvalidKeyException {
		byte[] bytes = Base64.getDecoder().decode(encodedKey);
		return decodePublicKey(bytes);
	}
	/**
	 * @param bytes The byte data that represents the public key
	 * @return The {@link PublicKey} represented by {@code bytes}
	 * @throws InvalidKeyException Thrown if the {@code bytes} is not a valid representation of a public key
	 */
	public static PublicKey decodePublicKey(byte[] bytes) throws InvalidKeyException {
		X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
		try {
			return KEY_FACTORY.generatePublic(spec);
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeyException("Wasn't able to generate public key", e);
		}
	}
}
