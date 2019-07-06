package me.retrodaredevil.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A non-blocking utility class to get input from an input stream usually by splitting lines
 */
public class InputStreamSplitter {
	private final InputStream inputStream;
	private final char splitChar;
	
	private final byte[] buffer = new byte[1024];
	private final Queue<String> lines = new LinkedList<>();
	private String currentLine = "";
	
	public InputStreamSplitter(InputStream inputStream, char splitChar) {
		this.inputStream = inputStream;
		this.splitChar = splitChar;
	}
	public InputStreamSplitter(InputStream inputStream){
		this(inputStream, '\n');
	}
	private void update() throws IOException {
		if(inputStream.available() > 0){
			int len = inputStream.read(buffer);
			String s = new String(buffer, 0, len);
			final StringBuilder currentLine = new StringBuilder(this.currentLine);
			for(char c : s.toCharArray()){
				if(c == splitChar){
					lines.add(currentLine.toString());
					currentLine.setLength(0);
				} else {
					currentLine.append(c);
				}
			}
			this.currentLine = currentLine.toString();
		}
	}
	public Queue<String> getQueue() throws IOException {
		update();
		return lines;
	}
}
