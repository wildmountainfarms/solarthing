package me.retrodaredevil.solarthing.couchdb;

import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.EktorpUtil;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.closed.authorization.PermissionObject;
import me.retrodaredevil.solarthing.packets.security.crypto.PublicKeyLookUp;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbAccessException;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;

public class CouchDbDocumentKeyMap implements PublicKeyLookUp {
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbDocumentKeyMap.class);

	private final CouchDbConnector connector;
	private final String documentName;

	private AuthorizationPacket authorizationPacket = null;
	private boolean notFound = false;
	private Long lastUpdate = null;

	public CouchDbDocumentKeyMap(CouchDbConnector connector, String documentName) {
		this.connector = connector;
		this.documentName = documentName;
	}
	public static CouchDbDocumentKeyMap createDefault(CouchProperties couchProperties) {
		final HttpClient httpClient = EktorpUtil.createHttpClient(couchProperties);
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
		return new CouchDbDocumentKeyMap(new StdCouchDbConnector(SolarThingConstants.CLOSED_UNIQUE_NAME, instance), "authorized");
	}

	private void updatePacket() {
		try {
			authorizationPacket = connector.find(AuthorizationPacket.class, documentName);
			notFound = false;
		} catch (DocumentNotFoundException e) {
			notFound = true;
			LOGGER.info("Could not find document: " + documentName + " on database: " + connector.getDatabaseName());
		} catch (DbAccessException e) {
			LOGGER.error("Could not access database: " + connector.getDatabaseName(), e);
			notFound = false;
		}
		lastUpdate = System.currentTimeMillis();
	}

	@Override
	public PublicKey getKey(String sender) {
		Long lastUpdate = this.lastUpdate;
		if ((authorizationPacket == null && !notFound) || lastUpdate == null || lastUpdate + 30 * 1000 < System.currentTimeMillis()) { // update every 30 seconds if necessary
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
