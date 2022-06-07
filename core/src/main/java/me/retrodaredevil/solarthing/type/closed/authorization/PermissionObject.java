package me.retrodaredevil.solarthing.type.closed.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.security.crypto.InvalidKeyException;
import me.retrodaredevil.solarthing.packets.security.crypto.KeyUtil;

import java.security.PublicKey;

import static java.util.Objects.requireNonNull;

/**
 * Represents the permissions that a particular sender has. This used to contain actual permissions, but those were
 * never used, so they were removed. In the future permissions may be added to this object again, but they must be manually
 * checked each time a particular permission needs to be used.
 */
@JsonExplicit
@JsonIgnoreProperties({"fragments"}) // we need to allow this to be present for a few versions after we remove this to allow SolarThing instances to be updated to accept packets that do NOT have this property
public final class PermissionObject {
	private final String publicKey;
	private final PublicKey publicKeyObject;
	@JsonCreator
	public PermissionObject(
			@JsonProperty(value = "publicKey", required = true) String publicKey
	) throws InvalidKeyException {
		requireNonNull(this.publicKey = publicKey);
		publicKeyObject = KeyUtil.decodePublicKey(publicKey);
	}

	@JsonProperty("publicKey")
	public String getPublicKey() {
		return publicKey;
	}

	public PublicKey getPublicKeyObject() {
		return publicKeyObject;
	}

	@Deprecated
	@JsonProperty("fragments")
	private Object getFragments() {
		// We need to return something here for the resulting serialization to be compatible with older SolarThing version's deserialization.
		return null;
	}
}
