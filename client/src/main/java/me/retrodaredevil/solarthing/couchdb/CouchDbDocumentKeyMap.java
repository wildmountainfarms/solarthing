package me.retrodaredevil.solarthing.couchdb;

import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.solarthing.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.closed.authorization.PermissionObject;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.UpdateToken;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.security.crypto.PublicKeyLookUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;

public class CouchDbDocumentKeyMap implements PublicKeyLookUp {
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbDocumentKeyMap.class);
	private static final long UPDATE_PERIOD_MILLIS = 30 * 1000; // 30 seconds

	private final SolarThingDatabase database;

	private AuthorizationPacket authorizationPacket = null;
	private UpdateToken updateToken = null;
	private Long lastUpdate = null;

	public CouchDbDocumentKeyMap(SolarThingDatabase database) {
		this.database = database;
	}
	public static CouchDbDocumentKeyMap createDefault(CouchDbInstance couchDbInstance) {
		SolarThingDatabase database = CouchDbSolarThingDatabase.create(couchDbInstance);
		return new CouchDbDocumentKeyMap(database);
	}

	private void updatePacket() {
		lastUpdate = System.currentTimeMillis();
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
		Long lastUpdate = this.lastUpdate;
		if (lastUpdate == null || lastUpdate + UPDATE_PERIOD_MILLIS < System.currentTimeMillis()) {
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
