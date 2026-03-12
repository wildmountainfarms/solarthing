package me.retrodaredevil.solarthing.packets.security.crypto;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class InvalidKeyException extends CryptoException {
	public InvalidKeyException(String message) {
		super(message);
	}

	public InvalidKeyException(String message, Throwable cause) {
		super(message, cause);
	}
}
