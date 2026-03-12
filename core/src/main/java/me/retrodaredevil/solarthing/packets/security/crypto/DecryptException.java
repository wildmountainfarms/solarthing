package me.retrodaredevil.solarthing.packets.security.crypto;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class DecryptException extends CryptoException {
	public DecryptException(String message) {
		super(message);
	}

	public DecryptException(String message, Throwable cause) {
		super(message, cause);
	}
}
