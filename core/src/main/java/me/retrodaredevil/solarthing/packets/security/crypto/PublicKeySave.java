package me.retrodaredevil.solarthing.packets.security.crypto;

import java.security.PublicKey;

public interface PublicKeySave {
	void putKey(String sender, PublicKey key);
}
