package me.retrodaredevil.solarthing.netcat;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.EOFException;

public interface SimpleConnection extends AutoCloseable {
	@Nullable String pollLine() throws EOFException;
	void send(@NonNull String line) throws EOFException;
}
