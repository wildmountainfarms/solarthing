package me.retrodaredevil.solarthing.packets.security.crypto;

public interface SenderSaveNameGenerator {
	String getFileName(String sender);
	
	SenderSaveNameGenerator DEFAULT = sender -> "key_" + sender + ".publickey";
}
