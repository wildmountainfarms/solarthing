package me.retrodaredevil.solarthing.util;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
class JacksonUtilTest {
	@Test
	void preservesEnumNamesAndUnknownEnumFallback() {
		var mapper = JacksonUtil.defaultMapper();
		assertEquals("\"READY\"", mapper.writeValueAsString(Status.READY));
		assertEquals(Status.READY, mapper.readValue("\"READY\"", Status.class));
		assertEquals(Status.UNKNOWN, mapper.readValue("\"FUTURE\"", Status.class));
	}

	@Test
	void derivesLenientMapperWithoutChangingOriginal() {
		ObjectMapper strict = JacksonUtil.defaultMapper();
		var builder = strict.rebuild();
		ObjectMapper lenient = JacksonUtil.lenientMapper(builder).build();
		String json = "{\"value\":7,\"future\":true}";
		assertEquals(new Config(7), lenient.readValue(json, Config.class));
		assertThrows(UnrecognizedPropertyException.class, () -> strict.readValue(json, Config.class));
	}

	@Test
	void supportsJavaTimeAndConfigurationSyntax() {
		var mapper = JacksonUtil.defaultMapper();
		assertEquals("\"PT5M\"", mapper.writeValueAsString(Duration.ofMinutes(5)));
		assertEquals(Instant.EPOCH, mapper.readValue(mapper.writeValueAsString(Instant.EPOCH), Instant.class));
		ObjectMapper configMapper = JacksonUtil.jsonLikeTolerantMapper(JacksonUtil.configurationMapper(mapper.rebuild())).build();
		assertEquals(new Config(7), configMapper.readValue("{/* comment */ value:7}", Config.class));
	}
	@Test
	void rejectsExplicitNullForPrimitives() {
		var mapper = JacksonUtil.defaultMapper();
		var permissiveMapper = mapper.rebuild()
				.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES).build();
		String json = "{\"value\":null}";
		assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, Config.class));
		assertEquals(new Config(0), permissiveMapper.readValue(json, Config.class));
		assertEquals(new Config(0), mapper.readValue("{\"value\":0}", Config.class));
	}

	@Test
	void missingPrimitiveConstructorValueUsesZero() {
		assertEquals(new Config(0), JacksonUtil.defaultMapper().readValue("{}", Config.class));
	}

	public record Config(int value) {}

	public enum Status {
		READY,
		@JsonEnumDefaultValue UNKNOWN;

		@Override
		public String toString() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
}
