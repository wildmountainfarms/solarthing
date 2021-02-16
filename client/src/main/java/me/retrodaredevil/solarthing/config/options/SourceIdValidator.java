package me.retrodaredevil.solarthing.config.options;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

import static java.util.Objects.requireNonNull;

@UtilityClass
public final class SourceIdValidator {
	private SourceIdValidator() { throw new UnsupportedOperationException(); }

	public static @NotNull String validateSourceId(String sourceId) {
		requireNonNull(sourceId);
		if (sourceId.isEmpty()) {
			throw new IllegalArgumentException("The source ID cannot be empty!");
		}
		for (char c : sourceId.toCharArray()) {
			if (c < 32 || c > 126) {
				throw new IllegalArgumentException("Character: " + c + " is not allowed! (ascii " + ((int) c) + ")");
			}
		}
		if (!isValidCharacter(sourceId.toCharArray()[0])) {
			throw new IllegalArgumentException("Your source ID must start with a 0-9, A-Z, or a-z character");
		}
		return sourceId;
	}
	private static boolean isValidCharacter(char c) {
		return (48 <= c && c <= 57) || // 0-9
				(65 <= c && c <= 90) || // A-Z
				(97 <= c && c <= 122); // a-z
	}
}
