package me.retrodaredevil.solarthing.packets.security.crypto;

import org.jspecify.annotations.NullMarked;

import java.security.GeneralSecurityException;

@NullMarked
public class CryptoException extends GeneralSecurityException {
	public CryptoException(String message) {
		super(message);
	}

	public CryptoException(String message, Throwable cause) {
		super(message, cause);
	}
}
