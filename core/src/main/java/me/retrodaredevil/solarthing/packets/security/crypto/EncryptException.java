package me.retrodaredevil.solarthing.packets.security.crypto;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class EncryptException extends CryptoException {
	public EncryptException(String message) {
		super(message);
	}

	public EncryptException(String message, Throwable cause) {
		super(message, cause);
	}
}
