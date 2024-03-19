package me.retrodaredevil.solarthing.program.couchdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.json.jackson.JacksonJsonData;
import me.retrodaredevil.couchdbjava.request.ViewQuery;
import me.retrodaredevil.couchdbjava.request.ViewQueryParamsBuilder;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.couchdb.SolarThingCouchDb;

import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class CustomWmfCouchDbEdit20240318 {
	/**
	 * The very first bad data is from {@code 240318,05,14/30|zDw4/g} (tracer), which had a timestamp of 1710761245129. We will round down to the nearest second
	 */
	private static final Instant BAD_TIMESTAMP_START = Instant.ofEpochMilli(1710761245000L);
	/**
	 * The first last bad data is from {@code 240318,06,06/37|3iyxcA} (mate), which had a timestamp of 1710763772164. We will round up to the nearest second
	 */
	private static final Instant BAD_TIMESTAMP_END = Instant.ofEpochMilli(1710763773000L);
	private static final List<String> BAD_IDS = List.of(
			"3iyxcA", // Mate
			"RatxnA", // Rover
			"zDw4/g", // Tracer
			"Mf9gVg", // Temperature outdoor
			"tTSMQg"  // Temperature battery room
	);
	private final CouchDbInstance instance;
	private final PrintStream out;
	private final CouchDbSetupMain.Prompt prompt;

	public CustomWmfCouchDbEdit20240318(CouchDbInstance instance, PrintStream out, CouchDbSetupMain.Prompt prompt) {
		this.instance = requireNonNull(instance);
		this.out = requireNonNull(out);
		this.prompt = requireNonNull(prompt);
	}

	public int doCustom() throws CouchDbException {
		out.println("Going to do some stuff to shift a specific set of data");
		CouchDbDatabase database = instance.getDatabase(SolarThingConstants.STATUS_DATABASE);
		ViewQuery viewQuery = SolarThingCouchDb.createMillisNullView(
				new ViewQueryParamsBuilder()
						.startKey(BAD_TIMESTAMP_START.toEpochMilli())
						.endKey(BAD_TIMESTAMP_END.toEpochMilli())
						.includeDocs(true)
						.build()
		);
		ViewResponse response = database.queryView(viewQuery);
		for (ViewResponse.DocumentEntry row : response.getRows()) {
			if (BAD_IDS.stream().anyMatch(endId -> row.getId().endsWith("|" + endId))) {
				out.println(row.getId());
				final JsonNode json;
				try {
					json = CouchDbJacksonUtil.getNodeFrom(row.getDoc());
				} catch (JsonProcessingException e) {
					e.printStackTrace(out);
					out.println("Could not parse JSON data from " + row.getId() + ". Exiting");
					return 1;
				}
				ObjectNode object = json.deepCopy();
				long dateMillis = object.get("dateMillis").asLong();
				if (dateMillis < BAD_TIMESTAMP_START.toEpochMilli() || dateMillis > BAD_TIMESTAMP_END.toEpochMilli()) {
					out.println("Could not parse dateMillis for " + row.getId() + ". Exiting");
					return 1;
				}
				if (object.get("editReason") != null) {
					out.println("Document " + row.getId() + " has already been edited!");
					continue;
				}
				long newDateMillis = dateMillis + Duration.ofSeconds(60 * 60 + 2 * 60 + 10).toMillis(); // plus 1 hour, 2 minutes, 10 seconds
				object.set("dateMillis", new LongNode(newDateMillis));
				object.set("originalDateMillis", new LongNode(dateMillis));
				object.set("editReason", new TextNode("wmf-custom-2024-03-18"));
				database.putDocument(row.getId(), new JacksonJsonData(object));
			}
		}

		return 0;
	}
}
