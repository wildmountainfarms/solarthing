package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.database.cache.PacketCache;
import me.retrodaredevil.solarthing.database.cache.SimplePacketCache;
import me.retrodaredevil.solarthing.type.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.type.closed.authorization.PermissionObject;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.security.crypto.PublicKeyLookUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.time.Duration;

public class DatabaseDocumentKeyMap implements PublicKeyLookUp {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseDocumentKeyMap.class);

	private final PacketCache<AuthorizationPacket> packetCache;

	public DatabaseDocumentKeyMap(PacketCache<AuthorizationPacket> packetCache) {
		this.packetCache = packetCache;
	}
	public static DatabaseDocumentKeyMap createFromDatabase(SolarThingDatabase database) {
		return new DatabaseDocumentKeyMap(new SimplePacketCache<>(Duration.ofSeconds(30), createPacketSourceFromDatabase(database), true));
	}
	public static SimplePacketCache.PacketSource<AuthorizationPacket> createPacketSourceFromDatabase(SolarThingDatabase database) {
		return updateToken -> {
			try {
				return database.queryAuthorized(updateToken);
			} catch (SolarThingDatabaseException e) {
				throw new SimplePacketCache.QueryException(e);
			}
		};
	}

	@Override
	public PublicKey getKey(String sender) {
		AuthorizationPacket authorizationPacket = packetCache.getPacket();
		if (authorizationPacket == null) {
			LOGGER.debug("authorizationPacket is null");
			return null;
		}
		PermissionObject permissionObject = authorizationPacket.getSenderPermissions().get(sender);
		if (permissionObject == null) {
			LOGGER.info("No permission object for sender: " + sender);
			return null;
		}
		// we may use more of permission object in the future
		return permissionObject.getPublicKeyObject();
	}
}
