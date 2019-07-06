package me.retrodaredevil.solarthing.packets.security.crypto;

public class CryptoException extends Exception {
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
	
	public CryptoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
