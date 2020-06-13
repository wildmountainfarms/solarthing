package me.retrodaredevil.solarthing.packets.security.crypto;

import java.io.*;
import java.nio.file.Files;
import java.security.PublicKey;

import static java.util.Objects.requireNonNull;

@Deprecated
public class DirectoryKeyMap implements PublicKeyLookUp, PublicKeySave {
	private final File directory;
	private final SenderSaveNameGenerator senderFileName;
	public DirectoryKeyMap(File directory, SenderSaveNameGenerator senderFileName) {
		this.directory = requireNonNull(directory);
		this.senderFileName = requireNonNull(senderFileName);
	}
	public DirectoryKeyMap(File directory) {
		this(directory, SenderSaveNameGenerator.DEFAULT);
	}
	@Override
	public PublicKey getKey(String sender) {
		String fileName = senderFileName.getFileName(requireNonNull(sender));
		File file = new File(directory, fileName);
		if (!file.exists()) {
			return null;
		}
		final byte[] bytes;
		try {
			bytes = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			throw new RuntimeException("Unable to read the file for some reason...", e);
		}
		try {
			return KeyUtil.decodePublicKey(bytes);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("A saved key was invalid! file: " + file, e);
		}
	}

	@Override
	public void putKey(String sender, PublicKey key) {
		String fileName = senderFileName.getFileName(requireNonNull(sender));
		File file = new File(directory, fileName);
		//noinspection ResultOfMethodCallIgnored
		directory.mkdirs();
		final OutputStream output;
		try {
			output = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		byte[] bytes = key.getEncoded();
		try {
			output.write(bytes);
			output.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
