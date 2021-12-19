package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

@UtilityClass
public final class JarUtil {
	private JarUtil() { throw new UnsupportedOperationException(); }
	/*
	Note that there are plenty of places in this class where RuntimeExceptions could pop up. However, I've never had a use case where a
	 */

	public static Data getData() {
		URI uri = getJarFileUri();
		Long lastModified = getLastModified(uri);
		return new Data(uri, lastModified);
	}

	public static URI getJarFileUri() {
		try {
			return JarUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getJarFileName() {
		return getData().getJarFileName();
	}
	private static @Nullable Long getLastModified(URI uri) {
		final File file;
		try {
			file = new File(getJarFileUri());
		} catch (IllegalArgumentException ex) {
			return null;
		}
		long value = file.lastModified();
		if (value == 0) {
			return null;
		}
		return value;
	}
	public static class Data {
		private final URI uri;
		private final Long lastModified;

		private Data(URI uri, Long lastModified) {
			this.uri = uri;
			this.lastModified = lastModified;
		}
		public String getJarFileName() {
			String path = uri.getPath();
			String[] split = path.split("/");
			return split[split.length - 1];
		}

		public @Nullable Long getLastModified() {
			return lastModified;
		}
		public @Nullable Instant getLastModifiedInstantOrNull() {
			if (lastModified == null) {
				return null;
			}
			return Instant.ofEpochMilli(lastModified);
		}
	}
}
