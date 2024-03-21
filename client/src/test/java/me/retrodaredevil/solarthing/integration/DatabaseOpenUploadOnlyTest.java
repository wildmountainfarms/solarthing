package me.retrodaredevil.solarthing.integration;

import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUnauthorizedException;
import me.retrodaredevil.solarthing.SolarThingDatabaseType;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.UpdateToken;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.misc.device.CelsiusCpuTemperaturePacket;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPackets;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePackets;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@Tag("integration")
public class DatabaseOpenUploadOnlyTest {

	private static PacketCollection createSimplePacketCollection() {
		// Note that in a production environment, we would make sure that the packets in a given packet collection
		//   actually belong in a particular database, but for testing we don't care. (A CpuTemperaturePacket shouldn't end up in the events database)
		return PacketCollections.createFromPackets(
				Instant.now(),
				Arrays.asList(
						new CelsiusCpuTemperaturePacket(null, 20.2f, null),
						InstanceSourcePackets.create("default"),
						InstanceFragmentIndicatorPackets.create(1)
				),
				PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR,
				ZoneId.systemDefault()
		);
	}


	@ParameterizedTest
	@MethodSource("me.retrodaredevil.solarthing.integration.DatabaseService#all")
	void test(DatabaseService databaseService) throws CouchDbException, SolarThingDatabaseException {
		IntegrationSetup.setup(IntegrationUtil.createCouchDbInstance(databaseService, IntegrationUtil.DEFAULT_ADMIN_AUTH));

		// Uploader user
		CouchDbInstance uploaderInstance = IntegrationUtil.createCouchDbInstance(databaseService, IntegrationUtil.getAuthFor(SolarThingDatabaseType.UserType.UPLOADER));
		SolarThingDatabase database = CouchDbSolarThingDatabase.create(uploaderInstance);

		PacketCollection packetCollection = createSimplePacketCollection();
		UpdateToken updateToken = database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
		try {
			database.getOpenDatabase().uploadPacketCollection(packetCollection, updateToken);
		} catch (SolarThingDatabaseException solarThingDatabaseException) {
			// expect that we are unauthorized to change the document
			assertInstanceOf(CouchDbUnauthorizedException.class, solarThingDatabaseException.getCause());
		}
	}
}
