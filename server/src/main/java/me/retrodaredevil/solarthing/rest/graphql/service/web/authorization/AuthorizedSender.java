package me.retrodaredevil.solarthing.rest.graphql.service.web.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.type.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.type.closed.authorization.PermissionObject;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

/**
 * A DTO used in a GraphQL endpoint to expose {@link AuthorizationPacket}'s {@link PermissionObject} in a way that satisfies the GraphQL requirements for not using Maps
 */
public final class AuthorizedSender {
	private final String sender;
	private final PermissionObject data;

	public AuthorizedSender(String sender, PermissionObject data) {
		this.sender = requireNonNull(sender);
		this.data = requireNonNull(data);
	}

	@JsonProperty("sender")
	public @NonNull String getSender() {
		return sender;
	}

	@JsonProperty("data")
	public @NonNull PermissionObject getData() {
		return data;
	}
}
