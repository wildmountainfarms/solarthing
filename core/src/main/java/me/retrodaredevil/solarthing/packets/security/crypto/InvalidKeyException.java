package me.retrodaredevil.solarthing.packets.security.crypto;

public class InvalidKeyException extends CryptoException {
	public InvalidKeyException() {
	}

	public InvalidKeyException(String message) {
		super(message);
	}

	public InvalidKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidKeyException(Throwable cause) {
		super(cause);
	}
}
