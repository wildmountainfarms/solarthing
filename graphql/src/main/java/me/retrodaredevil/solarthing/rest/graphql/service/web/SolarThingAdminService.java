package me.retrodaredevil.solarthing.rest.graphql.service.web;

import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.exception.NotFoundSolarThingDatabaseException;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.rest.exceptions.DatabaseException;
import me.retrodaredevil.solarthing.rest.graphql.service.web.authorization.AuthorizedSender;
import me.retrodaredevil.solarthing.type.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.type.closed.authorization.PermissionObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class SolarThingAdminService {

	private final DatabaseProvider databaseProvider;

	public SolarThingAdminService(DatabaseProvider databaseProvider) {
		this.databaseProvider = databaseProvider;
	}

	@GraphQLQuery
	public @NotNull DatabaseAuthorization databaseAuthorize(@NotNull String username, @NotNull String password) {
		return databaseProvider.authorize(username, password);
	}

	@GraphQLQuery
	public @NotNull List<@NotNull AuthorizedSender> authorizedSenders() {
		final AuthorizationPacket authorizationPacket;
		try {
			authorizationPacket = databaseProvider.getDatabase(null).queryAuthorized().getPacket();
		} catch (SolarThingDatabaseException e) {
			throw new DatabaseException(e);
		}
		return authorizationPacket.getSenderPermissions().entrySet().stream().map(entry -> {
			String sender = entry.getKey();
			PermissionObject permissionObject = entry.getValue();
			return new AuthorizedSender(sender, permissionObject);
		}).collect(Collectors.toList());
	}
	@GraphQLMutation
	public void removeAuthorizedSender(@NotNull DatabaseAuthorization authorization, @NotNull String sender) {
		VersionedPacket<AuthorizationPacket> versionedPacket;
		try {
			versionedPacket = databaseProvider.getDatabase(null).queryAuthorized();
		} catch (NotFoundSolarThingDatabaseException e) {
			throw new NoSuchElementException("Sender: " + sender + " was not present in the authorized document! (There is not authorized document)");
		} catch (SolarThingDatabaseException e) {
			throw new DatabaseException(e);
		}
		Map<String, PermissionObject> originalSenderPermissions = versionedPacket.getPacket().getSenderPermissions();
		if (!originalSenderPermissions.containsKey(sender)) {
			throw new NoSuchElementException("Sender: " + sender + " was not present in the authorized document!");
		}
		Map<String, PermissionObject> senderPermissions = new HashMap<>(originalSenderPermissions);
		senderPermissions.remove(sender);
		AuthorizationPacket newPacket = new AuthorizationPacket(senderPermissions);
		try {
			databaseProvider.getDatabase(authorization).updateAuthorized(newPacket, versionedPacket.getUpdateToken());
		} catch (SolarThingDatabaseException e) {
			throw new DatabaseException(e);
		}
	}
}
