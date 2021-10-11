package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceIdEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
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
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class CommandManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();
	private static final Cipher CIPHER;

	static {
		try {
			CIPHER = Cipher.getInstance(KeyUtil.CIPHER_TRANSFORMATION);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	private final File keyDirectory;
	private final String sender;

	public CommandManager(File keyDirectory, String sender) {
		requireNonNull(this.keyDirectory = keyDirectory);
		requireNonNull(this.sender = sender);
	}

	public KeyPair getKeyPair() {
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
				throw new RuntimeException("Error writing keys", e);
			}
		}
		return requireNonNull(keyPair);
	}

	/**
	 * The timezone used comes from {@link TimeZoneEnvironment}. Source used comes from {@link SourceIdEnvironment}.
	 * @param injectEnvironment The InjectEnvironment to use. {@link InjectEnvironment#get(Class)} is called before the making of {@link Creator}, so {@link Creator#create(Instant)}
	 *                          will not call {@link InjectEnvironment#get(Class)}
	 * @param instanceTargetPacket The {@link InstanceTargetPacket} to indicate which fragments to target or null. If null, it is not added to the packet collection
	 * @param commandOpenPacket The command packet
	 * @return A creator to make a packet collection. When supplied with an {@link Instant} representing now, a packet collection is created.
	 */
	public Creator makeCreator(InjectEnvironment injectEnvironment, @Nullable InstanceTargetPacket instanceTargetPacket, CommandOpenPacket commandOpenPacket, PacketCollectionIdGenerator packetCollectionIdGenerator) {
		requireNonNull(injectEnvironment);
		// instanceTargetPacket may be null
		requireNonNull(commandOpenPacket);

		KeyPair keyPair = getKeyPair();
		String sourceId = injectEnvironment.get(SourceIdEnvironment.class).getSourceId();
		InstanceSourcePacket instanceSourcePacket = InstanceSourcePackets.create(sourceId);

		ZoneId zoneId = injectEnvironment.get(TimeZoneEnvironment.class).getZoneId();

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

	public interface Creator {
		PacketCollection create(Instant now);
	}
}
