package me.retrodaredevil.solarthing.integration;

import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbCodeException;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import me.retrodaredevil.solarthing.SolarThingDatabaseType;
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

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
public class DatabaseRejectOldPacketsTest {

	private static PacketCollection createSimplePacketCollection(Instant instant) {
		// Note that in a production environment, we would make sure that the packets in a given packet collection
		//   actually belong in a particular database, but for testing we don't care. (A CpuTemperaturePacket shouldn't end up in the events database)
		return PacketCollections.createFromPackets(
				instant,
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

		Instant pastInstant = Instant.now().minus(Duration.ofMinutes(30));
		Instant futureInstant = Instant.now().plus(Duration.ofMinutes(4));
		Instant tinyBitInTheFutureInstant = Instant.now().plus(Duration.ofMinutes(1));
		PacketCollection pastPacketCollection = createSimplePacketCollection(pastInstant);
		try {
			database.getStatusDatabase().uploadPacketCollection(pastPacketCollection, null);
			fail("We expect this to fail");
		} catch (SolarThingDatabaseException solarThingDatabaseException) {
			expectForbiddenResponse(solarThingDatabaseException);
		}
		try {
			database.getEventDatabase().uploadPacketCollection(pastPacketCollection, null);
			fail("We expect this to fail");
		} catch (SolarThingDatabaseException solarThingDatabaseException) {
			expectForbiddenResponse(solarThingDatabaseException);
		}

		try {
			database.getOpenDatabase().uploadPacketCollection(pastPacketCollection, null);
			fail("We expect this to fail");
		} catch (SolarThingDatabaseException solarThingDatabaseException) {
			expectForbiddenResponse(solarThingDatabaseException);
		}
		try {
			// future solarthing_open test
			database.getOpenDatabase().uploadPacketCollection(createSimplePacketCollection(futureInstant), null);
			fail("We expect this to fail");
		} catch (SolarThingDatabaseException solarThingDatabaseException) {
			expectForbiddenResponse(solarThingDatabaseException);
		}
		// We expect to be able to upload a tiny bit in the future to SolarThing open
		database.getOpenDatabase().uploadPacketCollection(createSimplePacketCollection(tinyBitInTheFutureInstant), null);
	}
	private static void expectForbiddenResponse(SolarThingDatabaseException solarThingDatabaseException) {
		// NOTE: The cause is an implementation detail of a SolarThingDatabaseException
		//   Remember that we don't want to rely on implementation details in actual code,
		//   but it's OK in a test like this.
		assertInstanceOf(CouchDbCodeException.class, solarThingDatabaseException.getCause());

		CouchDbCodeException e = (CouchDbCodeException) solarThingDatabaseException.getCause();

		// We expect to have thrown a forbidden error as described here: https://docs.couchdb.org/en/stable/ddocs/ddocs.html#validate-document-update-functions
		assertEquals(403, e.getCode());
		ErrorResponse errorResponse = e.getErrorResponse();
		assertNotNull(errorResponse);
		assertEquals("forbidden", errorResponse.getError());
		assertTrue(errorResponse.getReason().contains("dateMillis"));
	}
}
