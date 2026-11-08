package me.retrodaredevil.solarthing.rest.graphql.jackson;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.graphql.metadata.strategy.value.InputParsingException;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
class JacksonValueMapperTest {
	@Test
	void wrapsJacksonConversionErrorsAsInputParsingErrors() {
		JacksonValueMapper mapper = new JacksonValueMapper(JacksonUtil.defaultMapper());
		InputParsingException error = assertThrows(InputParsingException.class,
				() -> mapper.fromInput(Map.of("value", "not-a-number"), Map.class, GenericTypeReflector.annotate(Input.class)));
		assertInstanceOf(JacksonException.class, error.getCause());
	}

	public record Input(int value) {}
}
