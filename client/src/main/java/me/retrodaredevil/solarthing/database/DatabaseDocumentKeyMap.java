package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.closed.authorization.PermissionObject;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.security.crypto.PublicKeyLookUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.time.Duration;

public class DatabaseDocumentKeyMap implements PublicKeyLookUp {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseDocumentKeyMap.class);
	private static final long UPDATE_PERIOD_NANOS = Duration.ofSeconds(30).toNanos();

	private final SolarThingDatabase database;

	private AuthorizationPacket authorizationPacket = null;
	private UpdateToken updateToken = null;
	private Long lastUpdateNanos = null;

	public DatabaseDocumentKeyMap(SolarThingDatabase database) {
		this.database = database;
	}

	private void updatePacket() {
		lastUpdateNanos = System.nanoTime();
		final VersionedPacket<AuthorizationPacket> versionedPacket;
		try {
			versionedPacket = database.queryAuthorized(updateToken);
		} catch (SolarThingDatabaseException e) {
			LOGGER.error("Error getting authorization packet", e);
			return;
		}
		if (versionedPacket == null) {
			LOGGER.debug("Packet is the same since the last request");
			return;
		}
		updateToken = versionedPacket.getUpdateToken();
		authorizationPacket = versionedPacket.getPacket();
	}

	@Override
	public PublicKey getKey(String sender) {
		Long lastUpdateNanos = this.lastUpdateNanos;
		if (lastUpdateNanos == null || System.nanoTime() - lastUpdateNanos >= UPDATE_PERIOD_NANOS) {
			updatePacket();
		}
		AuthorizationPacket authorizationPacket = this.authorizationPacket;
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
