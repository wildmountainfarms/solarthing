package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

@UtilityClass
public final class JarUtil {
	private JarUtil() { throw new UnsupportedOperationException(); }
	/*
	Note that there are plenty of places in this class where RuntimeExceptions could pop up. However, I've never had a use case where a
	 */

	public static Data getData() {
		URI uri = getJarFileUri();
		Instant lastModified = getLastModified(uri);
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
		return getData().getJarFileNameOrNull();
	}
	private static @Nullable Instant getLastModified(URI uri) {
		final Path path = pathOrNull(uri);
		if (path == null) {
			return null;
		}
		final FileTime time;
		try {
			time = Files.getLastModifiedTime(path);
		} catch (IOException e) {
			return null;
		}
		return time.toInstant();
	}
	private static Path pathOrNull(URI uri) {
		try {
			return Path.of(uri);
		} catch (IllegalArgumentException | FileSystemNotFoundException e) {
			return null;
		}
	}

	public static class Data {
		private final URI uri;
		private final Instant lastModified;

		private Data(URI uri, Instant lastModified) {
			this.uri = uri;
			this.lastModified = lastModified;
		}
		public String getJarFileNameOrNull() {
			final Path path = pathOrNull(uri);
			if (path == null) {
				return null;
			}
			return path.getFileName().toString();
		}

		public @Nullable Instant getLastModifiedInstantOrNull() {
			return lastModified;
		}
	}
}
