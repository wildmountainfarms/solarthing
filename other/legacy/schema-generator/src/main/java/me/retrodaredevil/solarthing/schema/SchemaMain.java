package me.retrodaredevil.solarthing.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;

public class SchemaMain {
	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		JsonSchemaGenerator generator = new JsonSchemaGenerator(mapper);
		JsonNode node = generator.generateJsonSchema(SolarStatusPacket.class);
		String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
		System.out.println(result);
	}
}
