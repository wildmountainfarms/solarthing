package me.retrodaredevil.solarthing.integration;

import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.solarthing.SolarThingDatabaseType;
import me.retrodaredevil.solarthing.database.MillisQueryBuilder;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
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

import static org.junit.jupiter.api.Assertions.fail;

@Tag("integration")
public class DatabasePermissionTest {

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

		{
			// No auth
			CouchDbInstance noAuthInstance = IntegrationUtil.createCouchDbInstance(databaseService, CouchDbAuth.createNoAuth());
			SolarThingDatabase database = CouchDbSolarThingDatabase.create(noAuthInstance);
			database.getStatusDatabase().query(new MillisQueryBuilder().startKey(System.currentTimeMillis()).build()); // anyone can query status
			database.getEventDatabase().query(new MillisQueryBuilder().startKey(System.currentTimeMillis()).build()); // anyone can query events

			PacketCollection packetCollection = createSimplePacketCollection();
			try {
				database.getStatusDatabase().uploadPacketCollection(packetCollection, null);
				fail("It is expected that unauthorized users cannot upload!");
			} catch (SolarThingDatabaseException expected) {

			}
			try {
				database.getEventDatabase().uploadPacketCollection(packetCollection, null);
				fail("It is expected that unauthorized users cannot upload!");
			} catch (SolarThingDatabaseException expected) {

			}
			database.getOpenDatabase().uploadPacketCollection(packetCollection, null); // anyone can upload to the open database
		}
		{
			// Uploader user
			CouchDbInstance uploaderInstance = IntegrationUtil.createCouchDbInstance(databaseService, IntegrationUtil.getAuthFor(SolarThingDatabaseType.UserType.UPLOADER));
			SolarThingDatabase database = CouchDbSolarThingDatabase.create(uploaderInstance);

			PacketCollection packetCollection = createSimplePacketCollection();
			database.getStatusDatabase().uploadPacketCollection(packetCollection, null);
			database.getEventDatabase().uploadPacketCollection(packetCollection, null);
		}
		{
			// Manager user
			CouchDbInstance managerInstance = IntegrationUtil.createCouchDbInstance(databaseService, IntegrationUtil.getAuthFor(SolarThingDatabaseType.UserType.MANAGER));
			SolarThingDatabase database = CouchDbSolarThingDatabase.create(managerInstance);

			PacketCollection packetCollection = createSimplePacketCollection();
			try {
				database.getStatusDatabase().uploadPacketCollection(packetCollection, null);
				fail("It is expected that manager users cannot upload!");
			} catch (SolarThingDatabaseException expected) {

			}
			// since 2ff15e82 (Feb 2022), the manager user is allowed to upload to the events database, but not the status database.
			database.getEventDatabase().uploadPacketCollection(packetCollection, null);
		}
	}
}
