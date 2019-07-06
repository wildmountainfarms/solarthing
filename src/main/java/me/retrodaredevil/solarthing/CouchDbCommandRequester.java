package me.retrodaredevil.solarthing;

import com.google.gson.JsonObject;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetriever;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.security.IntegrityPacket;
import me.retrodaredevil.solarthing.packets.security.SecurityPacket;
import me.retrodaredevil.solarthing.packets.security.SecurityPacketType;
import me.retrodaredevil.solarthing.packets.security.SecurityPackets;
import me.retrodaredevil.solarthing.packets.security.crypto.CryptoException;
import me.retrodaredevil.solarthing.packets.security.crypto.Decrypt;
import me.retrodaredevil.solarthing.packets.security.crypto.PublicKeyLookUp;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class CouchDbCommandRequester extends CouchDbPacketRetriever {
	private final PublicKeyLookUp publicKeyLookUp;
	
	private final Cipher cipher;
	
	public CouchDbCommandRequester(CouchProperties properties, PublicKeyLookUp publicKeyLookUp) {
		super(properties, "commands");
		this.publicKeyLookUp = publicKeyLookUp;
		
		try {
			cipher = Cipher.getInstance("RSA");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void receivePackets(List<JsonObject> jsonPackets) {
		List<PacketCollection> packets = new ArrayList<>(jsonPackets.size());
		long minTime = System.currentTimeMillis() - 5 * 60 * 1000; // last 5 minutes allowed
		for(JsonObject packet : jsonPackets){
			PacketCollection packetCollection = PacketCollections.createFromJson(packet, SecurityPackets::createFromJson);
			if(packetCollection.getDateMillis() < minTime){
				continue;
			}
			packets.add(packetCollection);
		}
		for(PacketCollection packetCollection : packets){
			for(Packet packet : packetCollection.getPackets()){
				SecurityPacket securityPacket = (SecurityPacket) packet;
				if(securityPacket.getPacketType() == SecurityPacketType.INTEGRITY_PACKET){
					IntegrityPacket integrityPacket = (IntegrityPacket) packet;
					try {
						String data = Decrypt.decrypt(cipher, publicKeyLookUp, integrityPacket);
					} catch (CryptoException e) {
						throw new RuntimeException(e); // TODO actually handle this
					}
				}
			}
		}
	}
}
