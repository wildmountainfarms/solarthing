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
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
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
	private final TargetPredicate targetPredicate;

	private final SimplePacketGroupParser integrityParser;

	private final Cipher cipher;
	private final long listenStartTime;

	private final State state;

	/**
	 * @param publicKeyLookUp The {@link PublicKeyLookUp} to get the PublicKey for a received {@link IntegrityPacket}
	 * @param packetGroupReceiver Receives successfully decrypted messages
	 */
	public SecurityPacketReceiver(PublicKeyLookUp publicKeyLookUp, PacketGroupReceiver packetGroupReceiver, TargetPredicate targetPredicate, Collection<? extends Class<? extends DocumentedPacket>> packetClasses, long listenStartTime, State state) {
		this.publicKeyLookUp = publicKeyLookUp;
		this.packetGroupReceiver = packetGroupReceiver;
		this.targetPredicate = targetPredicate;
		this.listenStartTime = listenStartTime;
		this.state = state;

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

	public void receivePacketGroups(List<? extends PacketGroup> packetGroups) {
		LOGGER.debug("received packets! size: " + packetGroups.size());
		List<TargetPacketGroup> packets = new ArrayList<>(packetGroups.size());
		long minTime = System.currentTimeMillis() - 5 * 60 * 1000; // last 5 minutes allowed
		// We don't have a max time. If someone uploads a packet with a future date millis, then it will get handled immediately, then never again.
		//   The only downside to this is that if the program restarts, there's the possibility of that command being processed again because it might be picked up by a query.
		//   We won't worry about that, because we trust authenticated clients to not do that.
		for(PacketGroup packetGroup : packetGroups){
			if(packetGroup.getDateMillis() < minTime){
				LOGGER.debug("Ignoring old packet");
				continue;
			}
			TargetPacketGroup targetPacketGroup = PacketGroups.parseToTargetPacketGroup(packetGroup);
			if (targetPredicate.targets(targetPacketGroup, false)) {
				packets.add(targetPacketGroup);
			}
		}
		/*
		 * If someone sends multiple commands in a single PacketCollection, those commands might have the same time in millis,
		 * which we want to allow. If we just updated senderLastCommandMap directly, this would not be allowed.
		 */
		Map<String, Long> lastCommands = new HashMap<>();
		for(TargetPacketGroup packetGroup : packets){
			long packetGroupDateMillis = packetGroup.getDateMillis();
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
						String data = decryptData(minTime, lastCommands, sender, integrityPacket.getEncryptedData(), packetGroupDateMillis);
						handleMessage(data, sender);
					}
				} else if (packetType == SecurityPacketType.LARGE_INTEGRITY_PACKET) {
					LargeIntegrityPacket largeIntegrityPacket = (LargeIntegrityPacket) packet;
					String sender = largeIntegrityPacket.getSender();
					final String invalidSenderReason = sender == null ? "sender is null!" : SenderUtil.getInvalidSenderNameReason(sender);
					if(invalidSenderReason != null){
						LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, invalidSenderReason);
					} else {
						String encodedHash = decryptData(minTime, lastCommands, sender, largeIntegrityPacket.getEncryptedHash(), packetGroupDateMillis);
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
		state.senderLastCommandMap.putAll(lastCommands);
	}

	private String decryptData(long minTime, Map<String, Long> lastCommands, String sender, String base64EncodedData, long expectedDateMillis) {
		final String data;
		try {
			data = Decrypt.decrypt(cipher, publicKeyLookUp, sender, base64EncodedData);
		} catch (DecryptException e) {
			// TODO This spams the summary log, which we don't want.
			//   Maybe we need to use VersionedPacket<T>s to make sure that we don't process packets twice, even if (especially if) they were not authenticated properly
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Someone tried to impersonate " + sender + "! Or that person has a new public key.", e);
			return null;
		} catch (InvalidKeyException e) {
			throw new RuntimeException("If there is a saved key, it should be valid! sender: " + sender, e);
		} catch (NotAuthorizedException e) {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, sender + " is not authorized!", e);
			return null;
		}
		final String[] split = data.split(",", 2);
		LOGGER.debug("decrypted data: " + data);
		if(split.length != 2){
			LOGGER.warn("split.length: " + split.length + " split: " + Arrays.asList(split));
			return null;
		}
		String hexMillis = split[0];
		String message = split[1];
		final long dateMillis;
		try {
			dateMillis = Long.parseLong(hexMillis, 16);
		} catch (NumberFormatException e){
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "Error parsing hex date millis", e);
			return null;
		}
		if (dateMillis != expectedDateMillis) {
			// Although we trust clients that are authenticated, we want to make sure that the
			//   dateMillis they said, is actually what is encrypted for integrity.
			// Also note that this may stop old, old clients from being able to send commands. As of a few versions ago (few versions ago as of 2021.10.29)
			//   this may have stopped the commands from being processed because the encrypted dateMillis and expected dateMillis may have been generated/gotten at different times
			//   but that is not the case now.
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Encrypted dateMillis is not the same as the expected dateMillis. dateMillis (decrypted): " + dateMillis + ", expected dateMillis: " + expectedDateMillis);
			return null;
		}
		Long lastCommand = state.senderLastCommandMap.get(sender);
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
		if (targetPredicate.targets(targetPacketGroup, true)) {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Got packet targeting: " + targetPacketGroup.getTargetFragmentIds() + " " + targetSourceId + " with " + targetPacketGroup.getPackets().size() + " packets.");
			packetGroupReceiver.receivePacketGroup(sender, targetPacketGroup);
		} else {
			LOGGER.info("After second target check, the payload with integrity is not targeting us. More info may be available if logged.");
		}
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
	public static class State {
		private final Map<String, Long> senderLastCommandMap = new HashMap<>();

	}
}
