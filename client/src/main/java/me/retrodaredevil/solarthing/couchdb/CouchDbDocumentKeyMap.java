package me.retrodaredevil.solarthing.couchdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotFoundException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotModifiedException;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.response.DocumentData;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.closed.authorization.PermissionObject;
import me.retrodaredevil.solarthing.packets.security.crypto.PublicKeyLookUp;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;

public class CouchDbDocumentKeyMap implements PublicKeyLookUp {
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbDocumentKeyMap.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final CouchDbDatabase database;
	private final String documentName;

	private AuthorizationPacket authorizationPacket = null;
	private String lastRevision = null;
	private boolean notFound = false;
	private Long lastUpdate = null;

	public CouchDbDocumentKeyMap(CouchDbDatabase database, String documentName) {
		this.database = database;
		this.documentName = documentName;
	}
	public static CouchDbDocumentKeyMap createDefault(CouchDbInstance couchDbInstance) {
		return new CouchDbDocumentKeyMap(couchDbInstance.getDatabase(SolarThingConstants.CLOSED_UNIQUE_NAME), "authorized");
	}

	private void updatePacket() {
		DocumentData documentData = null;
		try {
			documentData = database.getDocumentIfUpdated(documentName, lastRevision);
			notFound = false;
			LOGGER.debug("Got new auth packet. senders: " + authorizationPacket.getSenderPermissions().keySet());
		} catch (CouchDbNotModifiedException ignored) {
			notFound = false;
		} catch (CouchDbNotFoundException e) {
			notFound = true;
			LOGGER.info("Could not find document: " + documentName + " on database: " + database.getName());
		} catch (CouchDbException e) {
			LOGGER.error("Could not access database: " + database.getName(), e);
			notFound = false;
		}
		if (documentData != null) {
			try {
				authorizationPacket = CouchDbJacksonUtil.readValue(MAPPER, documentData.getJsonData(), AuthorizationPacket.class);
				lastRevision = documentData.getRevision();
			} catch (JsonProcessingException e) {
				LOGGER.error("Could not parse JSON!", e);
			}
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
