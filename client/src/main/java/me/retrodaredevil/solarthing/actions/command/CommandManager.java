package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceIdEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePackets;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPackets;
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
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;

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

	public PacketCollection create(ActionEnvironment actionEnvironment, Collection<Integer> fragmentIdTargets, CommandOpenPacket commandOpenPacket) {
		KeyPair keyPair = getKeyPair();
		String sourceId = actionEnvironment.getInjectEnvironment().get(SourceIdEnvironment.class).getSourceId();
		InstanceSourcePacket instanceSourcePacket = InstanceSourcePackets.create(sourceId);
		InstanceTargetPacket instanceTargetPacket = InstanceTargetPackets.create(fragmentIdTargets);

		ZoneId zoneId = actionEnvironment.getInjectEnvironment().get(TimeZoneEnvironment.class).getZoneId();

		// ----

		PacketCollection encryptedCollection = PacketCollections.createFromPackets(
				Arrays.asList(commandOpenPacket, instanceSourcePacket, instanceTargetPacket),
				PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR, zoneId
		);
		final String payload;
		try {
			payload = MAPPER.writeValueAsString(encryptedCollection);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		String hashString = Long.toHexString(System.currentTimeMillis()) + "," + HashUtil.encodedHash(payload);
		final String encrypted;
		try {
			encrypted = Encrypt.encrypt(CIPHER, keyPair.getPrivate(), hashString);
		} catch (InvalidKeyException | EncryptException e) {
			throw new RuntimeException(e);
		}
		PacketCollection packetCollection = PacketCollections.createFromPackets(
				Arrays.asList(
						new ImmutableLargeIntegrityPacket(sender, encrypted, payload),
						instanceSourcePacket, instanceTargetPacket
				),
				PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR, zoneId
		);

		return packetCollection;
	}

}
