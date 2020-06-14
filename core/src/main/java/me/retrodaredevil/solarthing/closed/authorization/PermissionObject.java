package me.retrodaredevil.solarthing.closed.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.security.crypto.InvalidKeyException;
import me.retrodaredevil.solarthing.packets.security.crypto.KeyUtil;

import java.security.PublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public final class PermissionObject {
	private final String publicKey;
	private final PublicKey publicKeyObject;
	private final List<Integer> fragments;
	private final Map<String, List<String>> unmodifiablePermissions;

	@JsonCreator
	public PermissionObject(
			@JsonProperty(value = "publicKey", required = true) String publicKey,
			@JsonProperty(value = "fragments", required = true) List<Integer> fragments,
			@JsonProperty("permissions") Map<String, List<String>> permissions
	) throws InvalidKeyException {
		this.publicKey = publicKey;
		this.fragments = fragments;

		publicKeyObject = KeyUtil.decodePublicKey(publicKey);

		if (permissions != null) {
			Map<String, List<String>> permissionsMap = new HashMap<>();
			for (Map.Entry<String, List<String>> entry : permissions.entrySet()) {
				String fragmentString = entry.getKey();
				List<String> permissionList = entry.getValue();
				requireNonNull(fragmentString);
				requireNonNull(permissionList);
				if (!fragmentString.equals("global")) {
					final int fragmentId;
					try {
						fragmentId = Integer.parseInt(fragmentString);
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(fragmentString + " is not a valid fragment ID!");
					}
					String expectedString = "" + fragmentId;
					if (!expectedString.equals(fragmentString)) {
						throw new IllegalArgumentException("expectedString != fragmentString. expectedString=" + expectedString + " fragmentString=" + fragmentString + " (Are there leading 0s?)");
					}
				}
				for (String permission : permissionList) {
					if (permission == null) {
						throw new NullPointerException("There was a null element in " + fragmentString + "'s permissions! permissionList=" + permissionList);
					}
				}
				permissionsMap.put(fragmentString, Collections.unmodifiableList(permissionList));
			}
			unmodifiablePermissions = Collections.unmodifiableMap(permissionsMap);
		} else {
			unmodifiablePermissions = Collections.emptyMap();
		}
	}

	@JsonProperty("publicKey")
	public String getPublicKey() {
		return publicKey;
	}

	public PublicKey getPublicKeyObject() {
		return publicKeyObject;
	}

	/**
	 *
	 * @return A list of fragment IDs that this a sender has access to
	 */
	@JsonProperty("fragments")
	public @Nullable List<Integer> getFragments() {
		return fragments;
	}

	public List<String> getGlobalPermissions() {
		List<String> r = unmodifiablePermissions.get("global");
		if (r == null) {
			return Collections.emptyList();
		}
		return r;
	}
	public List<String> getFragmentPermissions(int fragmentId) {
		List<String> r = unmodifiablePermissions.get("" + fragmentId);
		if (r == null) {
			return Collections.emptyList();
		}
		return r;
	}
	@JsonProperty("permissions")
	public Map<String, List<String>> getPermissionsMap() {
		return unmodifiablePermissions;
	}
}
