package me.retrodaredevil.solarthing.io;

import java.io.InputStream;
import java.io.OutputStream;

public interface IOBundle {
	InputStream getInputStream();
	OutputStream getOutputStream();
}
