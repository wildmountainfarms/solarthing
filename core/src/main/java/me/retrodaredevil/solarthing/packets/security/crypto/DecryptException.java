package me.retrodaredevil.solarthing.packets.security.crypto;

public class DecryptException extends CryptoException {
	public DecryptException() {
	}

	public DecryptException(String message) {
		super(message);
	}

	public DecryptException(String message, Throwable cause) {
		super(message, cause);
	}

	public DecryptException(Throwable cause) {
		super(cause);
	}

}
