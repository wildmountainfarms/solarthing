package me.retrodaredevil.solarthing.log;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Plugin(name = "ServerSocket", category = "Core", elementType = "appender", printObject = true)
public class SocketServerAppender extends AbstractOutputStreamAppender<SocketServerAppender.ServerSocketManager> {

	private final Queue<Byte> outputQueue;
	private final List<Socket> activeSockets = new ArrayList<>();

	private final String bindAddress;
	private final int port;

	private final Thread acceptThread = new Thread(this::runManageServerSocket);
	private final Thread outputThread = new Thread(this::runOutputHandler);

	private final ServerSocket serverSocket;
	{
		acceptThread.setDaemon(true);
		outputThread.setDaemon(true);
		try {
			serverSocket = new ServerSocket();
		} catch (IOException e) {
			// The default constructor should never throw an exception because it doesn't really do anything
			throw new RuntimeException(e);
		}
	}

	private volatile boolean stopping = false;

	protected SocketServerAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush, Property[] properties, ServerSocketManager manager, Queue<Byte> outputQueue, String bindAddress, int port) {
		super(name, layout, filter, ignoreExceptions, immediateFlush, properties, manager);
		requireNonNull(this.outputQueue = outputQueue);
		this.bindAddress = bindAddress;
		this.port = port;
	}

	@PluginFactory
	public static SocketServerAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filters") Filter filter,
			@PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
			@PluginAttribute(value = "immediateFlush", defaultBoolean = true) boolean immediateFlush,
			@PluginAttribute("address") String bindAddress,
			@PluginAttribute("port") String port
	) {
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		if (bindAddress == null) {
			bindAddress = "localhost";
		}
		Queue<Byte> outputQueue = new ConcurrentLinkedDeque<>();
		OutputStream outputStream = new OutputStream() {
			@Override
			public void write(int i) {
				synchronized (outputQueue) {
					outputQueue.add((byte) i);
				}
			}
		};
		ServerSocketManager manager = new ServerSocketManager(outputStream, layout);

		return new SocketServerAppender(name, layout, filter, ignoreExceptions, immediateFlush, new Property[]{}, manager, outputQueue, bindAddress, Integer.parseInt(port));
	}

	@Override
	public void start() {
		super.start();
		acceptThread.start();
		outputThread.start();
	}

	@Override
	protected boolean stop(long timeout, TimeUnit timeUnit, boolean changeLifeCycleState) {
		stopping = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		acceptThread.interrupt();
		outputThread.interrupt();
		try {
			acceptThread.join(1000);
			outputThread.join(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return super.stop(timeout, timeUnit, changeLifeCycleState);
	}

	private void runManageServerSocket() {
		try {
			runManageServerSocketRaw();
		} catch (IOException e) {
			if (!stopping) {
				throw new RuntimeException(e);
			}
			// if we are stopping, then ignore IOExceptions
		}
	}

	private void runManageServerSocketRaw() throws IOException {
		serverSocket.bind(new InetSocketAddress(bindAddress, port));
		while (!Thread.currentThread().isInterrupted()) {
			Socket socket = serverSocket.accept();
			synchronized (activeSockets) {
				activeSockets.add(socket);
			}
		}
	}
	private void runOutputHandler() {
		while (!Thread.currentThread().isInterrupted()) {
			final byte[] b;
			synchronized (outputQueue) {
				b = new byte[outputQueue.size()];
			}
			for (int i = 0; i < b.length; i++) {
				b[i] = requireNonNull(outputQueue.poll(), "Got null! Size must not have been " + b.length + "!");
			}
			synchronized (activeSockets) {
				for (Iterator<Socket> iterator = activeSockets.iterator(); iterator.hasNext(); ) {
					Socket socket = iterator.next();
					try {
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(b);
					} catch (IOException e) {
						iterator.remove();
					}
				}
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	static class ServerSocketManager extends OutputStreamManager {

		ServerSocketManager(OutputStream os, Layout<?> layout) {
			super(os, "default", layout, true);
		}

	}
}
