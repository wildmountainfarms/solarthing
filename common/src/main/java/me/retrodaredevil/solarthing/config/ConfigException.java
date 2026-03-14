package me.retrodaredevil.solarthing.config;

import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * An exception caused by the user's configuration.
 */
@NullMarked
public class ConfigException extends RuntimeException {
	private final String userMessage;

	public ConfigException(String message, @Nullable Throwable cause) {
		this(message, cause, message);
	}

	public ConfigException(String message, @Nullable Throwable cause, String userMessage) {
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
