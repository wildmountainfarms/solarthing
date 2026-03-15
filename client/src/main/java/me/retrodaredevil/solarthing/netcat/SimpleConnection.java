package me.retrodaredevil.solarthing.netcat;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.EOFException;

@NullMarked
public interface SimpleConnection extends AutoCloseable {
	@Nullable String pollLine() throws EOFException;
	void send(String line) throws EOFException;
}
