package me.retrodaredevil.solarthing;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.security.*;
import me.retrodaredevil.solarthing.packets.security.crypto.*;
import me.retrodaredevil.util.json.JsonFile;
import org.lightcouch.CouchDbClient;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.*;

public class SecurityPacketReceiver implements JsonPacketReceiver{
	private final PublicKeyLookUp publicKeyLookUp;
	private final DataReceiver dataReceiver;
	private final PublicKeySave publicKeySave;
	
	private final Cipher cipher;
	
	private Map<String, Long> senderLastCommandMap = new HashMap<>();
	
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
		System.out.println("received packets! size: " + jsonPackets.size());
		List<PacketCollection> packets = new ArrayList<>(jsonPackets.size());
		long minTime = System.currentTimeMillis() - 5 * 60 * 1000; // last 5 minutes allowed
		for(JsonObject packet : jsonPackets){
			final PacketCollection packetCollection;
			try {
				packetCollection = PacketCollections.createFromJson(packet, SecurityPackets::createFromJson);
			} catch(Exception e){
				e.printStackTrace();
				System.err.println("tried to create a packet collection from: ");
				System.err.println(JsonFile.gson.toJson(packet));
				continue;
			}
			if(packetCollection.getDateMillis() < minTime){
				System.out.println("Ignoring old packet");
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
						System.err.println(invalidSenderReason);
					} else {
						try {
							String data = Decrypt.decrypt(cipher, publicKeyLookUp, integrityPacket);
							final String[] split = data.split(",", 2);
							System.out.println("decrypted data: " + data);
							if(split.length != 2){
								System.err.println("split.length: " + split.length + " split: " + Arrays.asList(split));
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
									if(dateMillis > System.currentTimeMillis()) {
										System.err.println("Message from " + sender + " is from the future???");
									} else if(dateMillis < minTime){
										System.err.println("Message from " + sender + " was parsed, but it too old!");
									} else if(lastCommand != null && dateMillis <= lastCommand) { // if this command is old or if someone is trying to send the exact same command twice
										System.err.println("Message from " + sender + " was parsed, but was older than the last command they sent!");
									} else {
										lastCommands.put(sender, dateMillis);
										dataReceiver.receiveData(sender, dateMillis, message);
									}
								}
							}
						} catch (DecryptException e) {
							e.printStackTrace();
							System.err.println("Someone tried to impersonate " + sender + "! Or that person has a new public key.");
						} catch (InvalidKeyException e) {
							throw new RuntimeException("If there is a saved key, it should be valid! sender: " + sender, e);
						} catch (NotAuthorizedException e) {
							e.printStackTrace();
							System.err.println(sender + " is not authorized!");
						}
					}
				} else if(packetType == SecurityPacketType.AUTH_NEW_SENDER){
					AuthNewSenderPacket auth = (AuthNewSenderPacket) packet;
					String sender = auth.getSender();
					PublicKey key = auth.getPublicKeyObject();
					final String invalidSenderReason = sender == null ? "sender is null!" : SenderUtil.getInvalidSenderNameReason(sender);
					if(invalidSenderReason != null){
						System.err.println(invalidSenderReason);
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
