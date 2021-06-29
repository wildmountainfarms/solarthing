package me.retrodaredevil.solarthing.netcat;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class ConnectionHandler {
	private final NetCatServerHandler netCatServerHandler;
	private final List<SimpleConnection> connections = new ArrayList<>();

	public ConnectionHandler(NetCatServerHandler netCatServerHandler) {
		this.netCatServerHandler = netCatServerHandler;
	}

	public void handleRequests(Function<? super @NotNull String, @Nullable String> function) {
		while (true) {
			SimpleConnection connection = netCatServerHandler.poll();
			if (connection == null) break;
			connections.add(connection);
		}
		for (Iterator<SimpleConnection> it = connections.iterator(); it.hasNext(); ) {
			SimpleConnection connection = it.next();
			while (true) {
				final String line;
				try {
					line = connection.pollLine();
				} catch (EOFException e) {
					it.remove();
					break;
				}
				if (line == null) {
					break;
				}
				try {
					String toSend = function.apply(line);
					if (toSend != null) {
						connection.send(toSend);
					}
				} catch (EOFException e) {
					it.remove();
					break;
				}
			}
		}
	}
}
