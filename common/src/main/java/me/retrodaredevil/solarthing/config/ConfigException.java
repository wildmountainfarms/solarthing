package me.retrodaredevil.solarthing.config;

/**
 * An exception caused by the user's configuration.
 */
public class ConfigException extends RuntimeException {
	private final String userMessage;

	public ConfigException(String message, Throwable cause) {
		this(message, cause, message);
	}

	public ConfigException(String message, Throwable cause, String userMessage) {
		super(message, cause);
		this.userMessage = userMessage;
	}
	public ConfigException(String message) {
		this(message, null);
	}

	public String getUserMessage() {
		return userMessage;
	}
}
