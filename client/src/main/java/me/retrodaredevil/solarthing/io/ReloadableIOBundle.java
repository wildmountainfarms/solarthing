package me.retrodaredevil.solarthing.io;

import me.retrodaredevil.io.IOBundle;
import me.retrodaredevil.solarthing.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReloadableIOBundle implements IOBundle {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReloadableIOBundle.class);
	private static final IOBundle BLANK_IO_BUNDLE = IOBundle.of(new InputStream() {
		@Override
		public int read() throws IOException {
			throw new NotInitializedIOException("IO not initialized!");
		}
	}, new OutputStream() {
		@Override
		public void write(int i) throws IOException {
			throw new NotInitializedIOException("IO not initialized!");
		}
	});
	private final IOBundleCreator creator;
	private @NotNull IOBundle ioBundle = BLANK_IO_BUNDLE;

	public ReloadableIOBundle(IOBundleCreator creator) {
		this.creator = creator;
		reload();
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public void close() throws Exception {
		ioBundle.close();
	}
	public void reload() {
		try {
			ioBundle.close();
		} catch (Exception e) {
			LOGGER.debug("Error while closing io bundle", e);
		}
		ioBundle = BLANK_IO_BUNDLE;
		try {
			ioBundle = creator.create();
			LOGGER.debug("Successfully reloaded IOBundle");
		} catch (Exception e) {
			LOGGER.error("Could not create IOBundle", e);
		}
	}
	private final InputStream inputStream = new InputStream() {
		@Override
		public int available() throws IOException {
			return ioBundle.getInputStream().available();
		}

		@Override
		public synchronized void mark(int readlimit) {
			ioBundle.getInputStream().mark(readlimit);
		}

		@Override
		public synchronized void reset() throws IOException {
			ioBundle.getInputStream().reset();
		}

		@Override
		public boolean markSupported() {
			return ioBundle.getInputStream().markSupported();
		}

		@Override
		public long skip(long n) throws IOException {
			return ioBundle.getInputStream().skip(n);
		}

		@Override
		public int read() throws IOException {
			return ioBundle.getInputStream().read();
		}

		@Override
		public int read(byte[] b) throws IOException {
			return ioBundle.getInputStream().read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return ioBundle.getInputStream().read(b, off, len);
		}

		@Override
		public void close() throws IOException {
			ioBundle.getInputStream().close();
		}
	};
	private final OutputStream outputStream = new OutputStream() {
		@Override
		public void write(int i) throws IOException {
			ioBundle.getOutputStream().write(i);
		}

		@Override
		public void write(byte[] b) throws IOException {
			ioBundle.getOutputStream().write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			ioBundle.getOutputStream().write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			ioBundle.getOutputStream().flush();
		}

		@Override
		public void close() throws IOException {
			ioBundle.getOutputStream().close();
		}
	};
	@FunctionalInterface
	public interface IOBundleCreator {
		IOBundle create() throws Exception;
	}
}
