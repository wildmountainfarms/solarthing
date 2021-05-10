package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.PacketGroupReceiver;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.collection.TargetPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.parsing.*;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.packets.security.IntegrityPacket;
import me.retrodaredevil.solarthing.packets.security.LargeIntegrityPacket;
import me.retrodaredevil.solarthing.packets.security.SecurityPacket;
import me.retrodaredevil.solarthing.packets.security.SecurityPacketType;
import me.retrodaredevil.solarthing.packets.security.crypto.*;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SecurityPacketReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityPacketReceiver.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();


	private final PublicKeyLookUp publicKeyLookUp;
	private final PacketGroupReceiver packetGroupReceiver;
	private final String sourceId;
	private final int fragmentId;

	private final SimplePacketGroupParser integrityParser;

	private final Cipher cipher;

	private final Map<String, Long> senderLastCommandMap = new HashMap<>();

	private final long listenStartTime;

	/**
	 * @param publicKeyLookUp The {@link PublicKeyLookUp} to get the PublicKey for a received {@link IntegrityPacket}
	 * @param packetGroupReceiver Receives successfully decrypted messages
	 * @param sourceId The source ID being used. Only accept from this source ID
	 * @param fragmentId The fragment ID being used. Only accept from this fragment ID
	 */
	public SecurityPacketReceiver(PublicKeyLookUp publicKeyLookUp, PacketGroupReceiver packetGroupReceiver, String sourceId, int fragmentId, Collection<? extends Class<? extends DocumentedPacket>> packetClasses) {
		this.publicKeyLookUp = publicKeyLookUp;
		this.packetGroupReceiver = packetGroupReceiver;
		this.sourceId = sourceId;
		this.fragmentId = fragmentId;

		List<Class<? extends DocumentedPacket>> classList = new ArrayList<>(packetClasses);
		classList.add(InstancePacket.class);
		ObjectMapper integrityMapper = MAPPER.copy();
		integrityMapper.getSubtypeResolver().registerSubtypes(Collections.unmodifiableList(classList));
		integrityParser = new SimplePacketGroupParser(integrityMapper, PacketParsingErrorHandler.DO_NOTHING);

		try {
			cipher = Cipher.getInstance(KeyUtil.CIPHER_TRANSFORMATION);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
		listenStartTime = System.currentTimeMillis();
	}

	public void receivePacketGroups(List<PacketGroup> packetGroups) {
		LOGGER.debug("received packets! size: " + packetGroups.size());
		List<TargetPacketGroup> packets = new ArrayList<>(packetGroups.size());
		long minTime = System.currentTimeMillis() - 5 * 60 * 1000; // last 5 minutes allowed
		for(PacketGroup packetGroup : packetGroups){
			if(packetGroup.getDateMillis() < minTime){
				LOGGER.info("Ignoring old packet");
				continue;
			}
			TargetPacketGroup targetPacketGroup = PacketGroups.parseToTargetPacketGroup(packetGroup);
			String packetSourceId = targetPacketGroup.getSourceId();
			if (!packetSourceId.equals(sourceId)) {
				LOGGER.debug("Received packet was for source: " + packetSourceId);
			} else if(!targetPacketGroup.isTarget(fragmentId)) {
				LOGGER.debug("Received packet wasn't for fragmentId: " + fragmentId + ". It was for these: " + targetPacketGroup.getTargetFragmentIds());
			} else {
				packets.add(targetPacketGroup);
			}
		}
		/*
		 * If someone sends multiple commands in a single PacketCollection, those commands might have the same time in millis,
		 * which we want to allow. If we just updated senderLastCommandMap directly, this would not be allowed.
		 */
		Map<String, Long> lastCommands = new HashMap<>();
		for(TargetPacketGroup packetGroup : packets){
			for(Packet packet : packetGroup.getPackets()){
				SecurityPacket securityPacket = (SecurityPacket) packet;
				SecurityPacketType packetType = securityPacket.getPacketType();
				if(packetType == SecurityPacketType.INTEGRITY_PACKET){
					IntegrityPacket integrityPacket = (IntegrityPacket) packet;
					String sender = integrityPacket.getSender();
					final String invalidSenderReason = sender == null ? "sender is null!" : SenderUtil.getInvalidSenderNameReason(sender);
					if(invalidSenderReason != null){
						LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, invalidSenderReason);
					} else {
						String data = decryptData(minTime, lastCommands, sender, integrityPacket.getEncryptedData());
						handleMessage(data, sender);
					}
				} else if (packetType == SecurityPacketType.LARGE_INTEGRITY_PACKET) {
					LargeIntegrityPacket largeIntegrityPacket = (LargeIntegrityPacket) packet;
					String sender = largeIntegrityPacket.getSender();
					final String invalidSenderReason = sender == null ? "sender is null!" : SenderUtil.getInvalidSenderNameReason(sender);
					if(invalidSenderReason != null){
						LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, invalidSenderReason);
					} else {
						String encodedHash = decryptData(minTime, lastCommands, sender, largeIntegrityPacket.getEncryptedHash());
						if (encodedHash != null) {
							byte[] decodedHash = null;
							try {
								decodedHash = Base64Variants.getDefaultVariant().decode(encodedHash);
							} catch (IllegalArgumentException e) {
								LOGGER.error("Not base64 data!", e);
							}
							if (decodedHash != null) {
								String payload = largeIntegrityPacket.getPayload();
								byte[] payloadHash = HashUtil.hash(payload);
								if (Arrays.equals(decodedHash, payloadHash)) {
									LOGGER.debug("Successfully compared hashes!");
									handleMessage(payload, sender);
								} else {
									LOGGER.warn("Unsuccessfully compared hashes! The data may have been tampered with!");
								}
							}
						}
					}
				}
			}
		}
		senderLastCommandMap.putAll(lastCommands);
	}

	private String decryptData(long minTime, Map<String, Long> lastCommands, String sender, String base64EncodedData) {
		try {
			String data = Decrypt.decrypt(cipher, publicKeyLookUp, sender, base64EncodedData);
			final String[] split = data.split(",", 2);
			LOGGER.debug("decrypted data: " + data);
			if(split.length != 2){
				LOGGER.warn("split.length: " + split.length + " split: " + Arrays.asList(split));
			} else {
				String hexMillis = split[0];
				String message = split[1];
				Long dateMillis = null;
				try {
					dateMillis = Long.parseLong(hexMillis, 16);
				} catch (NumberFormatException e){
					LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "Error parsing hex date millis", e);
				}
				if(dateMillis != null){
					Long lastCommand = senderLastCommandMap.get(sender);
					long currentTime = System.currentTimeMillis();
					if(dateMillis > currentTime + 5000) { // there's a 5 second grace period in case the clock is slightly off
						LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Message from " + sender + " is from the future??? dateMillis: " + dateMillis + " currentTime: " + currentTime);
						lastCommands.put(sender, dateMillis); // put this here anyway so it can't be used later
					} else if(dateMillis < minTime){
						LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Message from " + sender + " was parsed, but it too old! dateMillis: " + dateMillis + " minTime: " + minTime);
					} else if(lastCommand != null && dateMillis <= lastCommand) { // if this command is old or if someone is trying to send the exact same command twice
						LOGGER.debug("Message from " + sender + " was parsed, but was older than the last command they sent! dateMillis: " + dateMillis + " lastCommand: " + lastCommand);
					} else if(dateMillis < listenStartTime){
						LOGGER.debug(SolarThingConstants.SUMMARY_MARKER, "Message from " + sender + " was parsed, but it was sent before we started listening! dateMillis: " + dateMillis + " listenStartTime: " + listenStartTime);
					} else {
						lastCommands.put(sender, dateMillis);
						return message;
					}
				}
			}
		} catch (DecryptException e) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Someone tried to impersonate " + sender + "! Or that person has a new public key.", e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("If there is a saved key, it should be valid! sender: " + sender, e);
		} catch (NotAuthorizedException e) {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, sender + " is not authorized!", e);
		}
		return null;
	}
	private void handleMessage(String message, String sender) {
		JsonNode node = null;
		try {
			node = MAPPER.readTree(message);
		} catch (JsonProcessingException e) {
			LOGGER.warn("Couldn't parse message to JSON. message=" + message);
		}
		if (node != null) {
			if (node.isObject()) {
				ObjectNode objectNode = (ObjectNode) node;
				handleObjectNode(sender, objectNode);
			} else {
				LOGGER.warn("node is not an object! node: " + node);
			}
		}
	}

	//	private String decryptData()
	private void handleObjectNode(String sender, ObjectNode objectNode) {
		final PacketGroup packetGroup;
		try {
			packetGroup = integrityParser.parse(objectNode);
		} catch (PacketParseException e) {
			LOGGER.error("Unable to parse packet group", e);
			return;
		}
		TargetPacketGroup targetPacketGroup = PacketGroups.parseToTargetPacketGroup(packetGroup);
		String targetSourceId = targetPacketGroup.getSourceId();
		// We do the same checks as above just to double check. We already trust the sender here,
		//   but double checks never hurt.
		if (!targetSourceId.equals(sourceId)) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "This packet is for sourceId: " + targetSourceId);
		} else if (!targetPacketGroup.isTarget(fragmentId)) {
			LOGGER.info("Received packet wasn't for fragmentId: " + fragmentId + ". It was for these: " + targetPacketGroup.getTargetFragmentIds());
		} else {
			packetGroupReceiver.receivePacketGroup(sender, targetPacketGroup);
		}
	}
}
