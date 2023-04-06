package me.retrodaredevil.solarthing.packets.security.crypto;

import java.security.GeneralSecurityException;

public class CryptoException extends GeneralSecurityException {
	public CryptoException() {
	}

	public CryptoException(String message) {
		super(message);
	}

	public CryptoException(String message, Throwable cause) {
		super(message, cause);
	}

	public CryptoException(Throwable cause) {
		super(cause);
	}

}
