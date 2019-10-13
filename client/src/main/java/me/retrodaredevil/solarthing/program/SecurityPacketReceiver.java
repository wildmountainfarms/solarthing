package me.retrodaredevil.solarthing.program;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.DataReceiver;
import me.retrodaredevil.solarthing.JsonPacketReceiver;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.security.*;
import me.retrodaredevil.solarthing.packets.security.crypto.*;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.*;

public class SecurityPacketReceiver implements JsonPacketReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityPacketReceiver.class);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	
	private final PublicKeyLookUp publicKeyLookUp;
	private final DataReceiver dataReceiver;
	private final PublicKeySave publicKeySave;
	
	private final Cipher cipher;
	
	private final Map<String, Long> senderLastCommandMap = new HashMap<>();
	
	/**
	 *
	 * @param publicKeyLookUp The {@link PublicKeyLookUp} to get the PublicKey for a received {@link IntegrityPacket}
	 * @param dataReceiver The {@link DataReceiver} that receives data decrypted successfully from {@link IntegrityPacket}s
	 * @param publicKeySave The {@link PublicKeySave} that is called for all {@link AuthNewSenderPacket} packets. It is recommended not to authorize each request unless you confirm it.
	 */
	public SecurityPacketReceiver(PublicKeyLookUp publicKeyLookUp, DataReceiver dataReceiver, PublicKeySave publicKeySave) {
		this.publicKeyLookUp = publicKeyLookUp;
		this.dataReceiver = dataReceiver;
		this.publicKeySave = publicKeySave;
		
		try {
			cipher = Cipher.getInstance(KeyUtil.CIPHER_TRANSFORMATION);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void receivePackets(List<JsonObject> jsonPackets) {
		LOGGER.debug("received packets! size: " + jsonPackets.size());
		List<PacketCollection> packets = new ArrayList<>(jsonPackets.size());
		long minTime = System.currentTimeMillis() - 5 * 60 * 1000; // last 5 minutes allowed
		for(JsonObject packet : jsonPackets){
			final PacketCollection packetCollection;
			try {
				packetCollection = PacketCollections.createFromJson(packet, SecurityPackets::createFromJson);
			} catch(Exception e){
				LOGGER.error("tried to create a packet collection from: " + GSON.toJson(packet), e);
				continue;
			}
			if(packetCollection.getDateMillis() < minTime){
				LOGGER.info("Ignoring old packet");
				continue;
			}
			packets.add(packetCollection);
		}
		/*
		 * If someone sends multiple commands in a single PacketCollection, those commands might have the same time in millis,
		 * which we want to allow. If we just updated senderLastCommandMap directly, this would not be allowed.
		 */
		Map<String, Long> lastCommands = new HashMap<>();
		for(PacketCollection packetCollection : packets){
			for(Packet packet : packetCollection.getPackets()){
				SecurityPacket securityPacket = (SecurityPacket) packet;
				SecurityPacketType packetType = securityPacket.getPacketType();
				if(packetType == SecurityPacketType.INTEGRITY_PACKET){
					IntegrityPacket integrityPacket = (IntegrityPacket) packet;
					String sender = integrityPacket.getSender();
					final String invalidSenderReason = sender == null ? "sender is null!" : SenderUtil.getInvalidSenderNameReason(sender);
					if(invalidSenderReason != null){
						LOGGER.warn(invalidSenderReason);
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
									e.printStackTrace();
								}
								if(dateMillis != null){
									Long lastCommand = senderLastCommandMap.get(sender);
									long currentTime = System.currentTimeMillis();
									if(dateMillis > currentTime) {
										LOGGER.warn("Message from " + sender + " is from the future??? dateMillis: " + dateMillis + " currentTime: " + currentTime);
									} else if(dateMillis < minTime){
										LOGGER.warn("Message from " + sender + " was parsed, but it too old! dateMillis: " + dateMillis + " minTime: " + minTime);
									} else if(lastCommand != null && dateMillis <= lastCommand) { // if this command is old or if someone is trying to send the exact same command twice
										LOGGER.warn("Message from " + sender + " was parsed, but was older than the last command they sent! dateMillis: " + dateMillis + " lastCommand: " + lastCommand);
									} else {
										lastCommands.put(sender, dateMillis);
										dataReceiver.receiveData(sender, dateMillis, message);
									}
								}
							}
						} catch (DecryptException e) {
							LOGGER.warn("Someone tried to impersonate " + sender + "! Or that person has a new public key.", e);
						} catch (InvalidKeyException e) {
							throw new RuntimeException("If there is a saved key, it should be valid! sender: " + sender, e);
						} catch (NotAuthorizedException e) {
							LOGGER.info(sender + " is not authorized!", e);
						}
					}
				} else if(packetType == SecurityPacketType.AUTH_NEW_SENDER){
					AuthNewSenderPacket auth = (AuthNewSenderPacket) packet;
					String sender = auth.getSender();
					PublicKey key = auth.getPublicKeyObject();
					final String invalidSenderReason = sender == null ? "sender is null!" : SenderUtil.getInvalidSenderNameReason(sender);
					if(invalidSenderReason != null){
						LOGGER.warn(invalidSenderReason);
					} else {
						publicKeySave.putKey(sender, key);
					}
				}
			}
		}
		senderLastCommandMap.putAll(lastCommands);
	}
	public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, EncryptException, InvalidKeyException {
		CouchDbClient client = new CouchDbClient("commands", false, "http", "192.168.10.104", 5984, "admin", "relax");
		KeyPair pair = KeyUtil.generateKeyPair();
		String data = Long.toHexString(System.currentTimeMillis()) + ",GEN OFF";
		Cipher cipher = Cipher.getInstance(KeyUtil.CIPHER_TRANSFORMATION);
		String encryptedData = Encrypt.encrypt(cipher, pair.getPrivate(), data);
		client.save(PacketCollections.createFromPackets(
			Arrays.asList(new ImmutableIntegrityPacket("josh", encryptedData)),
//			Arrays.asList(new ImmutableAuthNewSenderPacket("josh", KeyUtil.encodePublicKey(pair.getPublic()))),
			PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR
		));
		new DirectoryKeyMap(new File("authorized")).putKey("josh", pair.getPublic());
	}
}
