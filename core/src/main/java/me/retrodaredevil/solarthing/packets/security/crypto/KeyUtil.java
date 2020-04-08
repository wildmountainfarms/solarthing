package me.retrodaredevil.solarthing.packets.security.crypto;

import com.fasterxml.jackson.core.Base64Variants;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Contains constants and utility methods for encoding keys and creating new keys
 */
public final class KeyUtil {
	private KeyUtil() { throw new UnsupportedOperationException(); }

	public static final String PAIR_ALGORITHM = "RSA";
	public static final String FACTORY_ALGORITHM = "RSA";
	public static final String CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	public static final int KEY_SIZE = 1024;

	private static final KeyPairGenerator KEY_PAIR_GENERATOR;
	private static final KeyFactory KEY_FACTORY;

	static {
		try {
			KEY_PAIR_GENERATOR = KeyPairGenerator.getInstance(PAIR_ALGORITHM);
			KEY_FACTORY = KeyFactory.getInstance(FACTORY_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("RSA algorithm doesn't exist???", e);
		}
		KEY_PAIR_GENERATOR.initialize(KEY_SIZE);
	}

	public static KeyPair generateKeyPair(){
		return KEY_PAIR_GENERATOR.generateKeyPair();
	}
	public static String encodePublicKey(PublicKey key){
//		return Base64.getEncoder().encodeToString(key.getEncoded());
		return Base64Variants.getDefaultVariant().encode(key.getEncoded());
	}

	/**
	 *
	 * @param encodedKey The base64 data where when decoded, represents the public key
	 * @return The {@link PublicKey} represented by {@code encodedKey}
	 * @throws InvalidKeyException Thrown if the {@code encodedKey} is not valid
	 */
	public static PublicKey decodePublicKey(String encodedKey) throws InvalidKeyException {
//		byte[] bytes = Base64.getDecoder().decode(encodedKey);
		byte[] bytes = Base64Variants.getDefaultVariant().decode(encodedKey);
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
	/**
	 * @param bytes The byte data that represents the private key
	 * @return The {@link PrivateKey} represented by {@code bytes}
	 * @throws InvalidKeyException Thrown if the {@code bytes} is not a valid representation of a public key
	 */
	public static PrivateKey decodePrivateKey(byte[] bytes) throws InvalidKeyException {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
		try {
			return KEY_FACTORY.generatePrivate(spec);
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeyException("Wasn't able to generate private key", e);
		}
	}
}
