package me.retrodaredevil.solarthing.packets.security.crypto;

public class EncryptException extends CryptoException {
	public EncryptException() {
	}
	
	public EncryptException(String message) {
		super(message);
	}
	
	public EncryptException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public EncryptException(Throwable cause) {
		super(cause);
	}
}
