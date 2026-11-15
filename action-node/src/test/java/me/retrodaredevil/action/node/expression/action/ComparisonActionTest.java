package me.retrodaredevil.action.node.expression.action;

import me.retrodaredevil.action.node.expression.ComparisonExpression;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
class ComparisonActionTest {

	@Test
	void test() {
		ObjectMapper mapper = JsonMapper.builder().build();
		assertEquals(ComparisonExpression.Operator.GREATER_THAN_OR_EQUAL, mapper.readValue("\">=\"", ComparisonExpression.Operator.class));
	}
}
