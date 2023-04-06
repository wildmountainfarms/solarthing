package me.retrodaredevil.solarthing.packets.security.crypto;

public class NotAuthorizedException extends CryptoException {
	public NotAuthorizedException() {
	}

	public NotAuthorizedException(String message) {
		super(message);
	}

	public NotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotAuthorizedException(Throwable cause) {
		super(cause);
	}

}
