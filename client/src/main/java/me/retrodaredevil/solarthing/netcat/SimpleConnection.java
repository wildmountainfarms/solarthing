package me.retrodaredevil.solarthing.netcat;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.io.EOFException;

public interface SimpleConnection extends AutoCloseable {
	@Nullable String pollLine() throws EOFException;
	void send(@NotNull String line) throws EOFException;
}
