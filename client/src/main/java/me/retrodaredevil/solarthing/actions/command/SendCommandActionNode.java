package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.CouchDbEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceIdEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;
import me.retrodaredevil.solarthing.commands.packets.open.RequestCommandPacket;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
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
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.requireNonNull;

@JsonTypeName("sendcommand")
public class SendCommandActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendCommandActionNode.class);
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
	private final List<Integer> fragmentIdTargets;
	private final RequestCommandPacket requestCommandPacket;
	private final String sender;

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	@JsonCreator
	public SendCommandActionNode(
			@JsonProperty(value = "directory", required = true) File keyDirectory,
			@JsonProperty("targets") List<Integer> fragmentIdTargets,
			@JsonProperty("command") String commandName,
			@JsonProperty("sender") String sender) {
		requireNonNull(this.keyDirectory = keyDirectory);
		requireNonNull(this.fragmentIdTargets = fragmentIdTargets);
		requestCommandPacket = new ImmutableRequestCommandPacket(commandName);
		requireNonNull(this.sender = sender);
	}

	private KeyPair getKeyPair() {
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

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		KeyPair keyPair = getKeyPair();
		String sourceId = actionEnvironment.getInjectEnvironment().get(SourceIdEnvironment.class).getSourceId();
		InstanceSourcePacket instanceSourcePacket = InstanceSourcePackets.create(sourceId);
		InstanceTargetPacket instanceTargetPacket = InstanceTargetPackets.create(fragmentIdTargets);

		CouchDbDatabaseSettings databaseSettings = actionEnvironment.getInjectEnvironment().get(CouchDbEnvironment.class).getDatabaseSettings();
		CouchDbInstance instance = CouchDbUtil.createInstance(databaseSettings.getCouchProperties(), databaseSettings.getOkHttpProperties());
		SolarThingDatabase database = CouchDbSolarThingDatabase.create(instance);
		TimeZone timeZone = actionEnvironment.getInjectEnvironment().get(TimeZoneEnvironment.class).getTimeZone();

		return Actions.createRunOnce(() -> {
			PacketCollection encryptedCollection = PacketCollections.createFromPackets(
					Arrays.asList(requestCommandPacket, instanceSourcePacket, instanceTargetPacket),
					PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR, timeZone
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
					PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR, TimeZone.getDefault()
			);
			executorService.execute(() -> {
				try {
					database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
					LOGGER.info("Uploaded command request document");
				} catch (SolarThingDatabaseException e) {
					LOGGER.error("Error while uploading document.", e);
				}
			});
		});
	}
}
