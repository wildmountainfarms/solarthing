package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class SolarThingEnvironment {
	private SolarThingEnvironment() { throw new UnsupportedOperationException(); }

	public static boolean isRunningInDocker() {
		return System.getenv("DOCKER") != null;
	}
	public static @Nullable String getGitCommitHash() {
		return emptyToNull(System.getenv("COMMIT_HASH"));
	}
	public static @Nullable String getRef() {
		return emptyToNull(System.getenv("REF"));
	}
	private static String emptyToNull(String s) {
		if (s.isEmpty()) {
			return null;
		}
		return s;
	}
}
