package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.packets.security.crypto.PublicKeyLookUp;

public class AuthorizationEnvironment {
	private final PublicKeyLookUp publicKeyLookUp;

	public AuthorizationEnvironment(PublicKeyLookUp publicKeyLookUp) {
		this.publicKeyLookUp = publicKeyLookUp;
	}

	public PublicKeyLookUp getPublicKeyLookUp() {
		return publicKeyLookUp;
	}
}
