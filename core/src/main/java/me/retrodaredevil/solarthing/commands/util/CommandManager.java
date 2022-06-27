package me.retrodaredevil.solarthing.commands.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionCreator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePackets;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPacket;
import me.retrodaredevil.solarthing.packets.security.ImmutableLargeIntegrityPacket;
import me.retrodaredevil.solarthing.packets.security.crypto.*;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class CommandManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();
	private static final Cipher CIPHER = KeyUtil.createCipher();

	private final Supplier<KeyPair> keyPairSupplier;
	private final String sender;

	public CommandManager(File keyDirectory, String sender) {
		this(() -> getKeyPairFromDirectory(keyDirectory), sender);
	}
	public CommandManager(Supplier<KeyPair> keyPairSupplier, String sender) {
		requireNonNull(this.keyPairSupplier = keyPairSupplier);
		requireNonNull(this.sender = sender);
	}

	public String getSender() {
		return sender;
	}
	public KeyPair getKeyPair() {
		return keyPairSupplier.get();
	}

	public static KeyPair getKeyPairFromDirectory(File keyDirectory) {
		File publicKeyFile = new File(keyDirectory, ".publickey");
		File privateKeyFile = new File(keyDirectory, ".privatekey");
		KeyPair keyPair;
		try {
			PublicKey publicKey = KeyUtil.decodePublicKey(Files.readAllBytes(publicKeyFile.toPath()));
			PrivateKey privateKey = KeyUtil.decodePrivateKey(Files.readAllBytes(privateKeyFile.toPath()));
			keyPair = new KeyPair(publicKey, privateKey);
		} catch (IOException | InvalidKeyException e) {
			if (e instanceof NoSuchFileException) {
				LOGGER.info("Public or private key not found. Creating new ones");
			} else {
				LOGGER.error("Error while reading public or private key. Going to generate a new one.", e);
			}
			keyPair = KeyUtil.generateKeyPair();
			try {
				//noinspection ResultOfMethodCallIgnored
				keyDirectory.mkdirs(); // we don't care what the result of this is, we'll catch the exception if something goes wrong
				Files.write(publicKeyFile.toPath(), keyPair.getPublic().getEncoded(), StandardOpenOption.CREATE);
				Files.write(privateKeyFile.toPath(), keyPair.getPrivate().getEncoded(), StandardOpenOption.CREATE);
			} catch (IOException ioException) {
				// TODO use more specific exception here
				throw new RuntimeException("Error writing keys", ioException);
			}
		}
		return requireNonNull(keyPair);
	}

	/**
	 * @param instanceTargetPacket The {@link InstanceTargetPacket} to indicate which fragments to target or null. If null, it is not added to the packet collection
	 * @param commandOpenPacket The command packet
	 * @return A creator to make a packet collection. When supplied with an {@link Instant} representing now, a packet collection is created.
	 */
	public PacketCollectionCreator makeCreator(String sourceId, ZoneId zoneId, @Nullable InstanceTargetPacket instanceTargetPacket, CommandOpenPacket commandOpenPacket, PacketCollectionIdGenerator packetCollectionIdGenerator) {
		// instanceTargetPacket may be null

		KeyPair keyPair = getKeyPair();
		InstanceSourcePacket instanceSourcePacket = InstanceSourcePackets.create(sourceId);


		// ----
		return now -> {
			PacketCollection packetCollectionToNestAndEncrypt = PacketCollections.create(
					now,
					instanceTargetPacket == null
							? Arrays.asList(commandOpenPacket, instanceSourcePacket)
							: Arrays.asList(commandOpenPacket, instanceSourcePacket, instanceTargetPacket),
					"unused document ID that does not get serialized"
			);
			// Note, on packetCollectionToNestAndEncrypt, _id is not serialized, so the generator and zoneId used above do NOT affect anything
			final String payload;
			try {
				payload = MAPPER.writeValueAsString(packetCollectionToNestAndEncrypt);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			String hashString = Long.toHexString(now.toEpochMilli()) + "," + HashUtil.encodedHash(payload);
			final String encrypted;
			try {
				synchronized (CIPHER) { // It's possible we could be in a multi-threaded environment, and you cannot have multiple threads using a single cipher at once
					encrypted = Encrypt.encrypt(CIPHER, keyPair.getPrivate(), hashString);
				}
			} catch (InvalidKeyException | EncryptException e) {
				throw new RuntimeException(e);
			}
			List<Packet> packets = new ArrayList<>(Arrays.asList(
					new ImmutableLargeIntegrityPacket(sender, encrypted, payload),
					instanceSourcePacket
			));
			if (instanceTargetPacket != null) {
				packets.add(instanceTargetPacket);
			}
			return PacketCollections.createFromPackets(now, packets, packetCollectionIdGenerator, zoneId);
		};

	}

}
