package me.retrodaredevil.solarthing.closed.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.PacketEntry;

import java.util.Map;

@JsonExplicit
public final class AuthorizationPacket implements PacketEntry {
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
		return "authorized";
	}
}
