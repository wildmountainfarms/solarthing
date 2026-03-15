package me.retrodaredevil.solarthing.packets.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.solarthing.PacketTestUtil;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbStoredIdentifier;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class StoredIdentifierTest {

	@Test
	void testJsonMapping() throws JsonProcessingException {
		PacketTestUtil.testJson(new CouchDbStoredIdentifier(Instant.now().toEpochMilli(), "220202,20,03/20|VZSaSQ", "8-3de96027ddf38ebfae286567c31fec44"), StoredIdentifier.class, true);
	}
}
