package me.retrodaredevil.solarthing.rest.graphql.service.web;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.database.MillisQueryBuilder;
import me.retrodaredevil.solarthing.database.SessionInfo;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.exception.NotFoundSolarThingDatabaseException;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import me.retrodaredevil.solarthing.packets.security.AuthNewSenderPacket;
import me.retrodaredevil.solarthing.packets.security.crypto.InvalidKeyException;
import me.retrodaredevil.solarthing.packets.security.crypto.SenderUtil;
import me.retrodaredevil.solarthing.rest.exceptions.DatabaseException;
import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.SimpleNode;
import me.retrodaredevil.solarthing.rest.graphql.service.web.authorization.AuthorizedSender;
import me.retrodaredevil.solarthing.type.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.type.closed.authorization.PermissionObject;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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
	public @Nullable String username(@Nullable DatabaseAuthorization authorization) {
		// Allow authorization to be nullable, because it is valid to call getSessionInfo() without being logged in
		final SessionInfo sessionInfo;
		try {
			sessionInfo = databaseProvider.getDatabase(authorization).getSessionInfo();
		} catch (SolarThingDatabaseException e) {
			throw new DatabaseException(e);
		}
		return sessionInfo.getUsername();
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
	@GraphQLQuery
	public @NotNull List<@NotNull SimpleNode<AuthNewSenderPacket>> authRequests() {
		Instant startTime = Instant.now().minus(Duration.ofHours(4));
		final List<StoredPacketGroup> rawPackets;
		try {
			rawPackets = databaseProvider.getDatabase(null).getOpenDatabase().query(new MillisQueryBuilder().startKey(startTime.toEpochMilli()).build());
		} catch (SolarThingDatabaseException e) {
			throw new DatabaseException(e);
		}
		List<SimpleNode<AuthNewSenderPacket>> r = new ArrayList<>();
		for (StoredPacketGroup storedPacketGroup : rawPackets) {
			long dateMillis = storedPacketGroup.getDateMillis();
			storedPacketGroup.getPackets().stream()
					.filter(packet -> packet instanceof AuthNewSenderPacket)
					.map(packet -> (AuthNewSenderPacket) packet)
					.map(packet -> new SimpleNode<>(packet, dateMillis))
					.forEachOrdered(r::add);
		}

		return r;
	}
	private static VersionedPacket<AuthorizationPacket> queryAuthorizationPacket(SolarThingDatabase authorizedDatabase, String sender) {
		try {
			// This query doesn't require an authorization, but might as well give it because then we will fail early
			return authorizedDatabase.queryAuthorized();
		} catch (@SuppressWarnings("UnusedException") NotFoundSolarThingDatabaseException e) {
			// We don't need to provide the exception, because this particular exception can only mean one thing: The document does not exist in the database
			throw new NoSuchElementException("Sender: " + sender + " was not present in the authorized document! (There is no authorized document)");
		} catch (SolarThingDatabaseException e) {
			throw new DatabaseException(e);
		}
	}
	@GraphQLMutation
	public void removeAuthorizedSender(@NotNull DatabaseAuthorization authorization, @NotNull String sender) {
		SolarThingDatabase database = databaseProvider.getDatabase(authorization);
		VersionedPacket<AuthorizationPacket> versionedPacket = queryAuthorizationPacket(database, sender);
		Map<String, PermissionObject> originalSenderPermissions = versionedPacket.getPacket().getSenderPermissions();
		if (!originalSenderPermissions.containsKey(sender)) {
			throw new NoSuchElementException("Sender: " + sender + " was not present in the authorized document!");
		}
		Map<String, PermissionObject> senderPermissions = new HashMap<>(originalSenderPermissions);
		senderPermissions.remove(sender);
		AuthorizationPacket newPacket = new AuthorizationPacket(senderPermissions);
		try {
			database.updateAuthorized(newPacket, versionedPacket.getUpdateToken());
		} catch (SolarThingDatabaseException e) {
			throw new DatabaseException(e);
		}
	}
	@GraphQLMutation
	public void addAuthorizedSender(@NotNull DatabaseAuthorization authorization, @NotNull String sender, @NotNull String publicKey, @GraphQLArgument(name = "allowReplace", defaultValue = "false") boolean allowReplace) {
		{
			String invalidSenderReason = SenderUtil.getInvalidSenderNameReason(sender);
			if (invalidSenderReason != null) {
				throw new IllegalArgumentException("Invalid sender! " + invalidSenderReason);
			}
		}
		final PermissionObject newPermissionObject;
		try {
			// validate the given public key early
			newPermissionObject = new PermissionObject(publicKey);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException(e);
		}
		SolarThingDatabase database = databaseProvider.getDatabase(authorization);
		VersionedPacket<AuthorizationPacket> versionedPacket = queryAuthorizationPacket(database, sender);
		Map<String, PermissionObject> originalSenderPermissions = versionedPacket.getPacket().getSenderPermissions();
		if (!allowReplace && originalSenderPermissions.containsKey(sender)) {
			throw new IllegalArgumentException("You have tried to *replace* sender when you have allowReplace=false! sender: " + sender);
		}

		Map<String, PermissionObject> senderPermissions = new HashMap<>(originalSenderPermissions);
		senderPermissions.put(sender, newPermissionObject);
		AuthorizationPacket newPacket = new AuthorizationPacket(senderPermissions);
		try {
			database.updateAuthorized(newPacket, versionedPacket.getUpdateToken());
		} catch (SolarThingDatabaseException e) {
			throw new DatabaseException(e);
		}
	}
}
