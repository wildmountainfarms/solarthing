package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.PacketGroupReceiver;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.commands.event.SecurityRejectPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.DateMillisStoredIdentifier;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.collection.StoredIdentifier;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.TargetPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParseException;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParsingErrorHandler;
import me.retrodaredevil.solarthing.packets.collection.parsing.SimplePacketGroupParser;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import me.retrodaredevil.solarthing.packets.security.AuthNewSenderPacket;
import me.retrodaredevil.solarthing.packets.security.IntegrityPacket;
import me.retrodaredevil.solarthing.packets.security.LargeIntegrityPacket;
import me.retrodaredevil.solarthing.packets.security.crypto.*;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;

public class SecurityPacketReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityPacketReceiver.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();
	/** The amount of time into the future that a request is allowed to be made. */
	private static final Duration FUTURE_GRACE_PERIOD = Duration.ofSeconds(60);


	private final PublicKeyLookUp publicKeyLookUp;
	private final PacketGroupReceiver packetGroupReceiver;
	private final TargetPredicate targetPredicate;

	private final SimplePacketGroupParser integrityParser;

	private final Cipher cipher;
	private final long listenStartTime;

	private final Map<String, Long> senderLastCommandMap = new HashMap<>();
	private final NavigableSet<StoredIdentifier> processed = new TreeSet<>(); // Uses StoredIdentifier's compareTo

	/**
	 * @param publicKeyLookUp The {@link PublicKeyLookUp} to get the PublicKey for a received {@link IntegrityPacket}
	 * @param packetGroupReceiver Receives successfully decrypted messages
	 */
	public SecurityPacketReceiver(PublicKeyLookUp publicKeyLookUp, PacketGroupReceiver packetGroupReceiver, TargetPredicate targetPredicate, Collection<? extends Class<? extends DocumentedPacket>> packetClasses, long listenStartTime) {
		this.publicKeyLookUp = publicKeyLookUp;
		this.packetGroupReceiver = packetGroupReceiver;
		this.targetPredicate = targetPredicate;
		this.listenStartTime = listenStartTime;

		List<Class<? extends DocumentedPacket>> classList = new ArrayList<>(packetClasses);
		classList.add(InstancePacket.class);
		ObjectMapper integrityMapper = MAPPER.copy();
		integrityMapper.getSubtypeResolver().registerSubtypes(Collections.unmodifiableList(classList));

		// Use a DO_NOTHING error handler, because many instances of SecurityPacketReceiver will only have one or a few packetClasses.
		//   So it's actually very likely that parsing a packet will fail often
		integrityParser = new SimplePacketGroupParser(integrityMapper, PacketParsingErrorHandler.DO_NOTHING);

		try {
			cipher = Cipher.getInstance(KeyUtil.CIPHER_TRANSFORMATION);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	private void reject(StoredPacketGroup storedPacketGroup, SecurityRejectPacket.Reason reason, String moreInfo) {

	}
	private void accept(StoredPacketGroup storedPacketGroup) {

	}

	public void receivePacketGroups(List<StoredPacketGroup> packetGroups) {
		LOGGER.debug("received packets! size: " + packetGroups.size());
		long minTime = System.currentTimeMillis() - 5 * 60 * 1000; // last 5 minutes allowed
		processed.headSet(new DateMillisStoredIdentifier(minTime), false).clear(); // Remove data that we don't need to look at anymore
		// We don't have a max time. If someone uploads a packet with a future date millis, then it will get handled immediately, then never again.
		//   The only downside to this is that if the program restarts, there's the possibility of that command being processed again because it might be picked up by a query.
		//   We won't worry about that, because we trust authenticated clients to not do that.
		for(StoredPacketGroup packetGroup : packetGroups){
			if(packetGroup.getDateMillis() < minTime){
				LOGGER.debug("Ignoring old packet: " + packetGroup.getStoredIdentifier());
				continue;
			}
			if (packetGroup.getDateMillis() < listenStartTime) {
				LOGGER.debug("Ignoring packet before we started listening: " + packetGroup.getStoredIdentifier());
				continue;
			}
			if (processed.contains(packetGroup.getStoredIdentifier())) {
				LOGGER.debug("Ignoring already processed packet: " + packetGroup.getStoredIdentifier());
				continue;
			}
			TargetPacketGroup targetPacketGroup = PacketGroups.parseToTargetPacketGroup(packetGroup);
			if (targetPredicate.targets(targetPacketGroup, false)) {
				processed.add(packetGroup.getStoredIdentifier());
				receivePacketGroup(packetGroup, targetPacketGroup);
			}
		}
	}
	private void receivePacketGroup(StoredPacketGroup storedPacketGroup, TargetPacketGroup packetGroup) {
		LOGGER.debug("Receiving packet group: " + storedPacketGroup.getStoredIdentifier());
		long packetGroupDateMillis = packetGroup.getDateMillis();
		List<? extends Packet> packetGroupPackets = packetGroup.getPackets();
		if (packetGroupPackets.size() != 1) {
			/*
			In any "millis database" we have the documents inside follow the packet collection format.
			Packet collections can have many packets inside them. When we are dealing with security packets,
			we want to be able to easily refer to a single security packet. Because no one should ever need
			to contain multiple security packets in a given packet collection, we will only process
			packet collections with a single security packet. This allows things like SecurityEventPackets to work nicely,
			and doesn't have any real drawbacks except for some (possible) future use case that we don't know of.
			 */

			LOGGER.warn("This packetGroup targeting us had a packetGroup.size != 1! stored identifier: " + storedPacketGroup.getStoredIdentifier());
			reject(storedPacketGroup, SecurityRejectPacket.Reason.INVALID_DATA, "You cannot have more than one packet (besides source and target)");
			return;
		}
		Packet packet = packetGroupPackets.stream().findFirst().orElseThrow(() -> new AssertionError("size should be 1! This should not fail"));
		if (!(packet instanceof LargeIntegrityPacket)) {
			if (packet instanceof IntegrityPacket) {
				LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Got an IntegrityPacket! This is no longer supported!");
				reject(storedPacketGroup, SecurityRejectPacket.Reason.LEGACY_REQUEST, "IntegrityPacket is no longer supported");
			} else if (packet instanceof AuthNewSenderPacket) {
				LOGGER.warn("Got an AuthNewSenderPacket. Ignoring.");
				reject(storedPacketGroup, SecurityRejectPacket.Reason.UNKNOWN_ERROR, "Got auth new sender packet");
			} else {
				LOGGER.warn("Unknown packet: " + packet);
				reject(storedPacketGroup, SecurityRejectPacket.Reason.UNKNOWN_ERROR, "Got unknown packet");
			}
			return;
		}
		LargeIntegrityPacket largeIntegrityPacket = (LargeIntegrityPacket) packet;
		String sender = largeIntegrityPacket.getSender();
		final String invalidSenderReason = sender == null ? "sender is null!" : SenderUtil.getInvalidSenderNameReason(sender);
		if(invalidSenderReason != null){
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, invalidSenderReason);
			reject(storedPacketGroup, SecurityRejectPacket.Reason.INVALID_DATA, invalidSenderReason);
			return;
		}
		String encodedHash = decryptData(storedPacketGroup, sender, largeIntegrityPacket.getEncryptedHash(), packetGroupDateMillis);
		if (encodedHash == null) {
			// If encodedHash is null, then decryptData should have already called reject()
			return;
		}
		byte[] decodedHash;
		try {
			decodedHash = Base64Variants.getDefaultVariant().decode(encodedHash);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Not base64 data!", e);
			reject(storedPacketGroup, SecurityRejectPacket.Reason.INVALID_DATA, "Not base64 data!");
			return;
		}
		String payload = largeIntegrityPacket.getPayload();
		byte[] payloadHash = HashUtil.hash(payload);
		if (!Arrays.equals(decodedHash, payloadHash)) {
			LOGGER.warn("Unsuccessfully compared hashes! The data may have been tampered with!");
			reject(storedPacketGroup, SecurityRejectPacket.Reason.DIFFERENT_PAYLOAD, "Unsuccessfully compared hashes");
			return;
		}
		LOGGER.debug("Successfully compared hashes!");
		handleMessage(storedPacketGroup, payload, sender);
	}

	private String decryptData(StoredPacketGroup storedPacketGroup, String sender, String base64EncodedData, long expectedDateMillis) {
		final String data;
		try {
			data = Decrypt.decrypt(cipher, publicKeyLookUp, sender, base64EncodedData);
		} catch (DecryptException e) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Someone tried to impersonate " + sender + "! Or that person has a new public key.", e);
			reject(storedPacketGroup, SecurityRejectPacket.Reason.DECRYPT_ERROR, "Decrypt error");
			return null;
		} catch (InvalidKeyException e) {
			throw new RuntimeException("If there is a saved key, it should be valid! sender: " + sender, e);
		} catch (NotAuthorizedException e) {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, sender + " is not authorized!", e);
			reject(storedPacketGroup, SecurityRejectPacket.Reason.UNAUTHORIZED, "Sender: " + sender + " is not authorized");
			return null;
		}
		final String[] split = data.split(",", 2);
		LOGGER.debug("decrypted data: " + data);
		if(split.length != 2){
			// INVALID_DATA
			LOGGER.warn("split.length: " + split.length + " split: " + Arrays.asList(split));
			reject(storedPacketGroup, SecurityRejectPacket.Reason.INVALID_DATA, "split.length != 2");
			return null;
		}
		String hexMillis = split[0];
		String message = split[1];
		final long dateMillis;
		try {
			dateMillis = Long.parseLong(hexMillis, 16);
		} catch (NumberFormatException e){
			// INVALID_DATA
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "Error parsing hex date millis", e);
			reject(storedPacketGroup, SecurityRejectPacket.Reason.INVALID_DATA, "Error parsing hex date millis");
			return null;
		}
		if (dateMillis != expectedDateMillis) {
			// Although we trust clients that are authenticated, we want to make sure that the
			//   dateMillis they said, is actually what is encrypted for integrity.
			// Also note that this may stop old, old clients from being able to send commands. As of a few versions ago (few versions ago as of 2021.10.29)
			//   this may have stopped the commands from being processed because the encrypted dateMillis and expected dateMillis may have been generated/gotten at different times
			//   but that is not the case now.
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Encrypted dateMillis is not the same as the expected dateMillis. dateMillis (decrypted): " + dateMillis + ", expected dateMillis: " + expectedDateMillis);
			reject(storedPacketGroup, SecurityRejectPacket.Reason.DIFFERENT_PAYLOAD, "Different dateMillis values");
			return null;
		}
		Long lastCommand = senderLastCommandMap.get(sender);
		long currentTime = System.currentTimeMillis();
		if(dateMillis > currentTime + FUTURE_GRACE_PERIOD.toMillis()) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Message from " + sender + " is from the future??? dateMillis: " + dateMillis + " currentTime: " + currentTime);
			senderLastCommandMap.put(sender, dateMillis); // put this here anyway so it can't be used later
			reject(storedPacketGroup, SecurityRejectPacket.Reason.CLOCK_VARIANCE, "This request is from too far in the future");
			return null;
		} else if(lastCommand != null && dateMillis <= lastCommand) { // if this command is old or if someone is trying to send the exact same command twice
			LOGGER.debug("Message from " + sender + " was parsed, but was older than the last command they sent! dateMillis: " + dateMillis + " lastCommand: " + lastCommand);
			reject(storedPacketGroup, SecurityRejectPacket.Reason.CLOCK_VARIANCE, "This request is older than last sent command");
			return null;
		} else {
			senderLastCommandMap.put(sender, dateMillis);
			return message;
		}
		// CLOCK_VARIANCE
	}
	private void handleMessage(StoredPacketGroup storedPacketGroup, String message, String sender) {
		final JsonNode node;
		try {
			node = MAPPER.readTree(message);
		} catch (JsonProcessingException e) {
			LOGGER.warn("Couldn't parse message to JSON. message=" + message);
			reject(storedPacketGroup, SecurityRejectPacket.Reason.INVALID_DATA, "Could not parse JSON");
			return;
		}
		if (!node.isObject()) {
			// INVALID_DATA
			LOGGER.warn("node is not an object! node: " + node);
			reject(storedPacketGroup, SecurityRejectPacket.Reason.INVALID_DATA, "Parsed JSON is not a JSON object");
			return;
		}
		ObjectNode objectNode = (ObjectNode) node;
		handleObjectNode(storedPacketGroup, sender, objectNode);
	}

	//	private String decryptData()
	private void handleObjectNode(StoredPacketGroup storedPacketGroup, String sender, ObjectNode objectNode) {
		final PacketGroup packetGroup;
		try {
			packetGroup = integrityParser.parse(objectNode);
		} catch (PacketParseException e) {
			LOGGER.error("Unable to parse packet group", e);
			reject(storedPacketGroup, SecurityRejectPacket.Reason.PARSE_ERROR, "Could not deserialize ObjectNode to a PacketGroup");
			return;
		}
		TargetPacketGroup targetPacketGroup = PacketGroups.parseToTargetPacketGroup(packetGroup);
		String targetSourceId = targetPacketGroup.getSourceId();
		// We do the same checks as above just to double check. We already trust the sender here,
		//   but double checks never hurt.
		if (!targetPredicate.targets(targetPacketGroup, true)) {
			// DIFFERENT_PAYLOAD
			LOGGER.info("After second target check, the payload with integrity is not targeting us. More info may be available if logged.");
			reject(storedPacketGroup, SecurityRejectPacket.Reason.DIFFERENT_PAYLOAD, "Data discrepancy with targets");
			return;
		}
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Got packet targeting: " + targetPacketGroup.getTargetFragmentIds() + " " + targetSourceId + " with " + targetPacketGroup.getPackets().size() + " packets.");
		packetGroupReceiver.receivePacketGroup(sender, targetPacketGroup);
		accept(storedPacketGroup);
	}

	public interface TargetPredicate {
		boolean targets(TargetPacketGroup packetGroup, boolean isFromPayloadWithIntegrity);
	}
	public static class InstanceTargetPredicate implements TargetPredicate {
		private final String sourceId;
		private final int fragmentId;

		public InstanceTargetPredicate(String sourceId, int fragmentId) {
			this.sourceId = sourceId;
			this.fragmentId = fragmentId;
		}

		@Override
		public boolean targets(TargetPacketGroup packetGroup, boolean isFromPayloadWithIntegrity) {
			String packetSourceId = packetGroup.getSourceId();
			if (!packetSourceId.equals(sourceId)) {
				String message = "Received packet was for source: " + packetSourceId;
				if (isFromPayloadWithIntegrity) {
					LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, message);
				} else {
					LOGGER.debug(message);
				}
				if (packetSourceId.equals(InstanceSourcePacket.UNUSED_SOURCE_ID)) {
					LOGGER.warn("Parsed to a target packet group with an unused source ID! dateMillis: " + packetGroup.getDateMillis());
				}
				return false;
			}
			if(!packetGroup.isTarget(fragmentId)) {
				String message = "Received packet wasn't for fragmentId: " + fragmentId + ". It was for these: " + packetGroup.getTargetFragmentIds();
				if (isFromPayloadWithIntegrity) {
					LOGGER.info(message);
				} else {
					LOGGER.debug(message);
				}
				return false;
			}

			return true;
		}
	}
}
