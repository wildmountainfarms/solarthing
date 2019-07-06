package me.retrodaredevil.solarthing.packets.security.crypto;

import java.security.PublicKey;

public interface PublicKeyLookUp {
	PublicKey getKey(String sender);
}
