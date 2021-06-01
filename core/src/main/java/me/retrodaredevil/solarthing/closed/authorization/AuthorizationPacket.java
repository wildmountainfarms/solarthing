package me.retrodaredevil.solarthing.closed.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.PacketEntry;

import java.util.Map;

@JsonExplicit
@JsonIgnoreProperties(value = {"_id", "_rev"}, allowGetters = true)
public final class AuthorizationPacket implements PacketEntry {
	public static final String DOCUMENT_ID = "authorized";
	private final Map<String, PermissionObject> senderPermissions;

	@JsonCreator
	public AuthorizationPacket(
			@JsonProperty("senders") Map<String, PermissionObject> senderPermissions
	) {
		this.senderPermissions = senderPermissions;
	}

	@JsonProperty("senders")
	public Map<String, PermissionObject> getSenderPermissions() {
		return senderPermissions;
	}

	@Override
	public String getDbId() {
		return DOCUMENT_ID;
	}
}
