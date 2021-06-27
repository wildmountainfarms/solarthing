package me.retrodaredevil.solarthing.netcat;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

import static java.util.Objects.requireNonNull;

public class SocketSimpleConnection implements SimpleConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(SocketSimpleConnection.class);
	private final Socket socket;
	private final BufferedReader bufferedReader;
	private final PrintWriter writer;

	public SocketSimpleConnection(Socket socket) throws IOException {
		this.socket = socket;
		InputStream inputStream = socket.getInputStream();
		OutputStream outputStream = socket.getOutputStream();
		bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		writer = new PrintWriter(outputStream);
	}

	@Override
	public @Nullable String pollLine() throws EOFException {
		LOGGER.info("Polling");
		if (socket.isClosed()) {
			throw new EOFException("Socket is closed!");
		}
		try {
			if (bufferedReader.ready()) {
				LOGGER.info("Received");
				return bufferedReader.readLine();
			}
			return null;
		} catch (IOException e) {
			try {
				socket.close();
			} catch (IOException ignored) {
			}
			LOGGER.info("Had to close");
			throw new EOFException("Error while reading");
		}
	}

	@Override
	public void send(@NotNull String line) {
		LOGGER.info("Sending " + line);
		requireNonNull(line);
		writer.println(line);
		writer.flush();
	}

	@Override
	public void close() throws Exception {
		LOGGER.info("Closed");
		socket.close();
	}
}
