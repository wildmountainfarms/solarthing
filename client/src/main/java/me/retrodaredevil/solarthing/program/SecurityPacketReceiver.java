package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.DataReceiver;
import me.retrodaredevil.solarthing.JsonPacketReceiver;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.collection.parsing.ObjectMapperPacketConverter;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketGroupParser;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParserMultiplexer;
import me.retrodaredevil.solarthing.packets.collection.parsing.SimplePacketGroupParser;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import me.retrodaredevil.solarthing.packets.security.AuthNewSenderPacket;
import me.retrodaredevil.solarthing.packets.security.IntegrityPacket;
import me.retrodaredevil.solarthing.packets.security.SecurityPacket;
import me.retrodaredevil.solarthing.packets.security.SecurityPacketType;
import me.retrodaredevil.solarthing.packets.security.crypto.*;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.*;

public class SecurityPacketReceiver implements JsonPacketReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityPacketReceiver.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private static final PacketGroupParser PARSER = new SimplePacketGroupParser(new PacketParserMultiplexer(Arrays.asList(
			new ObjectMapperPacketConverter(MAPPER, SecurityPacket.class),
			new ObjectMapperPacketConverter(MAPPER, InstancePacket.class)
	), PacketParserMultiplexer.LenientType.FAIL_WHEN_UNHANDLED)); // This parser will fail if there's a packet it doesn't recognize

	private final PublicKeyLookUp publicKeyLookUp;
	private final DataReceiver dataReceiver;

	private final Cipher cipher;

	private final Map<String, Long> senderLastCommandMap = new HashMap<>();

	private final long listenStartTime;

	/**
	 *
	 * @param publicKeyLookUp The {@link PublicKeyLookUp} to get the PublicKey for a received {@link IntegrityPacket}
	 * @param dataReceiver The {@link DataReceiver} that receives data decrypted successfully from {@link IntegrityPacket}s
	 */
	public SecurityPacketReceiver(PublicKeyLookUp publicKeyLookUp, DataReceiver dataReceiver) {
		this.publicKeyLookUp = publicKeyLookUp;
		this.dataReceiver = dataReceiver;

		try {
			cipher = Cipher.getInstance(KeyUtil.CIPHER_TRANSFORMATION);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
		listenStartTime = System.currentTimeMillis();
	}

	@Override
	public void receivePackets(List<ObjectNode> jsonPackets) {
		LOGGER.debug("received packets! size: " + jsonPackets.size());
		List<PacketGroup> packets = new ArrayList<>(jsonPackets.size());
		long minTime = System.currentTimeMillis() - 5 * 60 * 1000; // last 5 minutes allowed
		for(ObjectNode packet : jsonPackets){
			final PacketGroup packetGroup;
			try {
				packetGroup = PARSER.parse(packet);
			} catch(Exception e){
				LOGGER.error("tried to create a packet collection from: " + packet, e);
				continue;
			}
			if(packetGroup.getDateMillis() < minTime){
				LOGGER.info("Ignoring old packet");
				continue;
			}
			PacketGroups.parseToInstancePacketGroup(packetGroup, new DefaultInstanceOptions(InstanceSourcePacket.UNUSED_SOURCE_ID, null));
			packets.add(packetGroup);
		}
		/*
		 * If someone sends multiple commands in a single PacketCollection, those commands might have the same time in millis,
		 * which we want to allow. If we just updated senderLastCommandMap directly, this would not be allowed.
		 */
		Map<String, Long> lastCommands = new HashMap<>();
		for(PacketGroup packetGroup : packets){
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
						try {
							String data = Decrypt.decrypt(cipher, publicKeyLookUp, integrityPacket);
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
									} else if(dateMillis < minTime){
										LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Message from " + sender + " was parsed, but it too old! dateMillis: " + dateMillis + " minTime: " + minTime);
									} else if(lastCommand != null && dateMillis <= lastCommand) { // if this command is old or if someone is trying to send the exact same command twice
										LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Message from " + sender + " was parsed, but was older than the last command they sent! dateMillis: " + dateMillis + " lastCommand: " + lastCommand);
									} else if(dateMillis < listenStartTime){
										LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Message from " + sender + " was parsed, but it was sent before we started listening! dateMillis: " + dateMillis + " listenStartTime: " + listenStartTime);
									} else {
										lastCommands.put(sender, dateMillis);
										dataReceiver.receiveData(sender, dateMillis, message);
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
					}
				}
			}
		}
		senderLastCommandMap.putAll(lastCommands);
	}
}
