package me.retrodaredevil.solarthing.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class GraphQLTests {
	@Test
	void test() throws JsonProcessingException {
		System.out.println(new ObjectMapper().writer().writeValueAsString(new TestJson()));
	}
	private class TestJson {
		public int getPVCurrent(){
			return 100;
		}
	}
}
