package me.retrodaredevil.solarthing.packets.security.crypto;

import org.jspecify.annotations.NullMarked;

import java.security.PublicKey;

@NullMarked
public interface PublicKeySave {
	void putKey(String sender, PublicKey key);
}
