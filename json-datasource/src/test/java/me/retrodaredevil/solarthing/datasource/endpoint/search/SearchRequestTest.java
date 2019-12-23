package me.retrodaredevil.solarthing.datasource.endpoint.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SearchRequestTest {
	@Test
	void testJson() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		SearchRequest object = mapper.readValue("{ \"type\": \"my_type\", \"target\": \"my_target\" }", SearchRequest.class);
		assertEquals("my_type", object.getType());
		assertEquals("my_target", object.getTarget());

		assertNull(mapper.readValue("{ \"target\": \"my_target\" }", SearchRequest.class).getType());
	}
}
