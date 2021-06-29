package me.retrodaredevil.solarthing.netcat;

import me.retrodaredevil.solarthing.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;

public class NetCatServerHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(NetCatServerHandler.class);
	private final ServerSocket serverSocket;
	private final Queue<SimpleConnection> connectionQueue = new ConcurrentLinkedDeque<>();

	public NetCatServerHandler(String bindAddress, int port) throws IOException {
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(bindAddress, port));

		Executors.newSingleThreadExecutor().execute(this::startLoop);
	}
	private void startLoop() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Socket socket = serverSocket.accept();
				connectionQueue.add(new SocketSimpleConnection(socket));
				LOGGER.info("Accepted connection");
			} catch (IOException e) {
				LOGGER.error("Couldn't accept a connection", e);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}
	public @Nullable SimpleConnection poll() {
		return connectionQueue.poll();
	}
}
